package br.com.tributos.financeiro.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.financeiro.adapters.out.pixbb.BbPixApiFalhaException;
import br.com.tributos.financeiro.adapters.out.pixbb.MapeadorStatusPixBb;
import br.com.tributos.financeiro.application.ports.GatewayPix;
import br.com.tributos.financeiro.application.ports.GatewayPix.ConsultaPixContexto;
import br.com.tributos.financeiro.application.ports.GatewayPix.PagamentoPix;
import br.com.tributos.financeiro.application.ports.GatewayPix.StatusCobrancaPix;
import br.com.tributos.financeiro.domain.GuiaArrecadacao;
import br.com.tributos.financeiro.domain.GuiaArrecadacaoRepository;
import br.com.tributos.financeiro.domain.OrigemConciliacaoPix;
import br.com.tributos.financeiro.domain.PixConciliacaoLog;
import br.com.tributos.financeiro.domain.PixConciliacaoLogRepository;
import br.com.tributos.financeiro.domain.SituacaoGuia;
import br.com.tributos.financeiro.domain.StatusPix;
import br.com.tributos.kernel.exception.ConfiguracaoInvalidaException;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.pixbb.ConfiguracaoPixBbPort;
import br.com.tributos.kernel.pixbb.ConfiguracaoPixOperacional;
import br.com.tributos.kernel.tenancy.TenantContext;
import tools.jackson.databind.ObjectMapper;

@Service
public class ConciliarPixService {

    private final GuiaArrecadacaoRepository guiaArrecadacaoRepository;
    private final ConfiguracaoPixBbPort configuracaoPixBbPort;
    private final GatewayPix gatewayPix;
    private final RegistrarPagamentoService registrarPagamentoService;
    private final PixConciliacaoLogRepository pixConciliacaoLogRepository;
    private final ObjectMapper objectMapper;

    public ConciliarPixService(
        GuiaArrecadacaoRepository guiaArrecadacaoRepository,
        ConfiguracaoPixBbPort configuracaoPixBbPort,
        GatewayPix gatewayPix,
        RegistrarPagamentoService registrarPagamentoService,
        PixConciliacaoLogRepository pixConciliacaoLogRepository,
        ObjectMapper objectMapper
    ) {
        this.guiaArrecadacaoRepository = guiaArrecadacaoRepository;
        this.configuracaoPixBbPort = configuracaoPixBbPort;
        this.gatewayPix = gatewayPix;
        this.registrarPagamentoService = registrarPagamentoService;
        this.pixConciliacaoLogRepository = pixConciliacaoLogRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public GuiaArrecadacao executar(UUID guiaId) {
        GuiaArrecadacao guia = guiaArrecadacaoRepository.buscarPorId(guiaId)
            .orElseThrow(() -> new NotFoundException("Guia de arrecadação não encontrada."));
        if (guia.pixTxid() == null || guia.pixTxid().isBlank()) {
            throw new ValidationException("Gere o PIX antes de conciliar.");
        }
        if (guia.situacao() == SituacaoGuia.PAGA) {
            return guia;
        }

        UUID tenantId = TenantContext.getObrigatorio();
        ConfiguracaoPixOperacional config = configuracaoPixBbPort.buscarAtiva(tenantId)
            .orElseThrow(() -> new ConfiguracaoInvalidaException(
                "Configuração PIX do Banco do Brasil não encontrada ou inativa."
            ));

        ConsultaPixContexto contexto = new ConsultaPixContexto(
            config.credenciais(),
            config.developerApplicationKey()
        );

        StatusCobrancaPix statusBb;
        try {
            statusBb = gatewayPix.consultarPorTxid(contexto, guia.pixTxid());
        } catch (BbPixApiFalhaException ex) {
            throw new ValidationException(ex.getMessage());
        }

        String statusAnterior = guia.statusPix() != null ? guia.statusPix().name() : guia.situacao().name();
        Optional<StatusPix> statusMapeado = MapeadorStatusPixBb.mapear(statusBb.estadoSolicitacao());
        String payloadBruto = serializar(statusBb);

        if (statusMapeado.isEmpty()) {
            gravarLog(guia, statusAnterior, statusBb.estadoSolicitacao(), payloadBruto, null);
            return guia;
        }

        StatusPix novoStatus = statusMapeado.get();
        GuiaArrecadacao resultado = guia;
        List<PagamentoPix> pagamentos = List.of();
        String endToEndId = null;

        if (novoStatus == StatusPix.CONCLUIDA) {
            try {
                pagamentos = gatewayPix.consultarPagamentos(contexto, guia.pixTxid());
            } catch (BbPixApiFalhaException ex) {
                throw new ValidationException(ex.getMessage());
            }
            payloadBruto = serializar(java.util.Map.of("status", statusBb, "pagamentos", pagamentos));

            if (!pagamentos.isEmpty()) {
                PagamentoPix pagamento = pagamentos.get(0);
                endToEndId = pagamento.endToEndId();
                BigDecimal valor = new BigDecimal(pagamento.valor());
                Instant horario = parseHorario(pagamento.horario());
                ResultadoLiquidacaoPix liquidacao = registrarPagamentoService.liquidacaoViaWebhook(
                    guia,
                    valor,
                    pagamento.endToEndId(),
                    horario
                );
                resultado = liquidacao.guia();
            } else {
                resultado = registrarPagamentoService.confirmarPix(guia.id());
            }
        } else if (novoStatus != guia.statusPix()) {
            resultado = atualizarStatusPix(guia, novoStatus);
        }

        gravarLog(resultado, statusAnterior, novoStatus.name(), payloadBruto, endToEndId);
        return resultado;
    }

    private GuiaArrecadacao atualizarStatusPix(GuiaArrecadacao guia, StatusPix statusPix) {
        GuiaArrecadacao atualizada = new GuiaArrecadacao(
            guia.id(),
            guia.tenantId(),
            guia.numero(),
            guia.tipoTributo(),
            guia.origemTipo(),
            guia.origemId(),
            guia.contribuinteId(),
            guia.competenciaMes(),
            guia.competenciaAno(),
            guia.dataEmissao(),
            guia.dataVencimento(),
            guia.valor(),
            guia.situacao(),
            guia.formaPagamentoId(),
            guia.dataEfetivacao(),
            guia.valorPago(),
            guia.codigoBarras(),
            guia.pixTxid(),
            guia.descricaoAvulsa(),
            guia.codigoVerificacao(),
            guia.tipoTributacao(),
            statusPix,
            guia.pixQrcodePayload(),
            guia.pixLink(),
            guia.pixEndToEndId(),
            guia.pixSolicitadoEm()
        );
        return guiaArrecadacaoRepository.salvar(atualizada);
    }

    private void gravarLog(
        GuiaArrecadacao guia,
        String statusAnterior,
        String statusNovo,
        String payloadBruto,
        String endToEndId
    ) {
        pixConciliacaoLogRepository.salvar(new PixConciliacaoLog(
            null,
            guia.tenantId(),
            guia.id(),
            guia.pixTxid(),
            endToEndId,
            statusAnterior,
            statusNovo,
            OrigemConciliacaoPix.CONSULTA,
            payloadBruto,
            Instant.now()
        ));
    }

    private String serializar(Object valor) {
        try {
            return objectMapper.writeValueAsString(valor);
        } catch (Exception ex) {
            return "{}";
        }
    }

    private static Instant parseHorario(String horario) {
        if (horario == null || horario.isBlank()) {
            return Instant.now();
        }
        try {
            return OffsetDateTime.parse(horario).toInstant();
        } catch (DateTimeParseException ex) {
            return Instant.now();
        }
    }
}
