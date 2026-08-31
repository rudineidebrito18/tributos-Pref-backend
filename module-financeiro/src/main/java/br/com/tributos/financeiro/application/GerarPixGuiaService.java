package br.com.tributos.financeiro.application;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.financeiro.adapters.out.pixbb.BbPixApiFalhaException;
import br.com.tributos.financeiro.adapters.out.pixbb.MontadorRequisicaoQrCodeBb;
import br.com.tributos.financeiro.application.ports.GatewayPix;
import br.com.tributos.financeiro.application.ports.GatewayPix.ComandoGerarQrCode;
import br.com.tributos.financeiro.application.ports.GatewayPix.RespostaQrCode;
import br.com.tributos.financeiro.domain.FormaPagamentoRepository;
import br.com.tributos.financeiro.domain.GuiaArrecadacao;
import br.com.tributos.financeiro.domain.GuiaArrecadacaoRepository;
import br.com.tributos.financeiro.domain.SituacaoGuia;
import br.com.tributos.financeiro.domain.StatusPix;
import br.com.tributos.kernel.cadastro.DevedorPixPort;
import br.com.tributos.kernel.exception.ConfiguracaoInvalidaException;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.pixbb.ConfiguracaoPixBbPort;
import br.com.tributos.kernel.pixbb.ConfiguracaoPixOperacional;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class GerarPixGuiaService {

    static final String CODIGO_FORMA_PIX = "PIX";

    private final GuiaArrecadacaoRepository guiaArrecadacaoRepository;
    private final FormaPagamentoRepository formaPagamentoRepository;
    private final ConfiguracaoPixBbPort configuracaoPixBbPort;
    private final DevedorPixPort devedorPixPort;
    private final GatewayPix gatewayPix;

    public GerarPixGuiaService(
        GuiaArrecadacaoRepository guiaArrecadacaoRepository,
        FormaPagamentoRepository formaPagamentoRepository,
        ConfiguracaoPixBbPort configuracaoPixBbPort,
        DevedorPixPort devedorPixPort,
        GatewayPix gatewayPix
    ) {
        this.guiaArrecadacaoRepository = guiaArrecadacaoRepository;
        this.formaPagamentoRepository = formaPagamentoRepository;
        this.configuracaoPixBbPort = configuracaoPixBbPort;
        this.devedorPixPort = devedorPixPort;
        this.gatewayPix = gatewayPix;
    }

    @Transactional
    public GuiaArrecadacao executar(UUID guiaId) {
        GuiaArrecadacao guia = buscarPendente(guiaId);
        if (guia.pixTxid() != null && guia.statusPix() == StatusPix.ATIVA) {
            return guia;
        }

        UUID tenantId = TenantContext.getObrigatorio();
        ConfiguracaoPixOperacional config = configuracaoPixBbPort.buscarAtiva(tenantId)
            .orElseThrow(() -> new ConfiguracaoInvalidaException(
                "Configuração PIX do Banco do Brasil não encontrada ou inativa."
            ));

        var formaPix = formaPagamentoRepository.buscarPorCodigo(CODIGO_FORMA_PIX)
            .orElseThrow(() -> new IllegalStateException("Forma de pagamento PIX não configurada."));

        var devedor = devedorPixPort.buscarPorPessoaId(guia.contribuinteId()).orElse(null);
        ComandoGerarQrCode comando = montarComando(guia, config, devedor);

        RespostaQrCode resposta;
        try {
            resposta = gatewayPix.gerarQrCode(comando);
        } catch (BbPixApiFalhaException ex) {
            throw new ValidationException(ex.getMessage());
        }

        GuiaArrecadacao atualizada = copiarGuia(
            guia,
            guia.situacao(),
            formaPix.id(),
            guia.codigoBarras(),
            resposta.txid(),
            StatusPix.ATIVA,
            resposta.qrCodePayload(),
            resposta.pixLink(),
            guia.pixEndToEndId(),
            Instant.now()
        );
        return guiaArrecadacaoRepository.salvar(atualizada);
    }

    private ComandoGerarQrCode montarComando(
        GuiaArrecadacao guia,
        ConfiguracaoPixOperacional config,
        br.com.tributos.kernel.cadastro.DadosDevedorPix devedor
    ) {
        String codigoGuia = resolverCodigoGuiaRecebimento(guia, config.indicadorCodigoBarras());
        String descricao = guia.descricaoAvulsa() != null && !guia.descricaoAvulsa().isBlank()
            ? guia.descricaoAvulsa()
            : "Guia " + guia.tipoTributo().name() + " nº " + guia.numero();

        String nomeDevedor = devedor != null ? devedor.nome() : null;
        String cpf = devedor != null ? normalizarDocumento(devedor.cpf()) : null;
        String cnpj = devedor != null ? normalizarDocumento(devedor.cnpj()) : null;

        return new ComandoGerarQrCode(
            config.credenciais(),
            config.developerApplicationKey(),
            config.numeroConvenio(),
            config.chavePix(),
            config.indicadorCodigoBarras(),
            guia.id(),
            guia.valor(),
            guia.dataVencimento(),
            codigoGuia,
            descricao,
            nomeDevedor,
            cpf,
            cnpj
        );
    }

    private static String normalizarDocumento(String documento) {
        if (documento == null || documento.isBlank()) {
            return null;
        }
        String apenasDigitos = documento.replaceAll("\\D", "");
        String semZeros = apenasDigitos.replaceFirst("^0+", "");
        return semZeros.isEmpty() ? "0" : semZeros;
    }

    static String resolverCodigoGuiaRecebimento(GuiaArrecadacao guia, String indicador) {
        if ("S".equalsIgnoreCase(indicador)) {
            if (guia.codigoBarras() == null || guia.codigoBarras().isBlank()) {
                throw new ValidationException("Guia sem código de barras para indicadorCodigoBarras = S.");
            }
            String digitos = guia.codigoBarras().replaceAll("\\D", "");
            if (digitos.length() != 44) {
                throw new ValidationException("Código de barras da guia deve ter 44 dígitos.");
            }
            return digitos;
        }
        if (guia.codigoVerificacao() == null || guia.codigoVerificacao().isBlank()) {
            throw new ValidationException("Guia sem código de verificação para PIX.");
        }
        return guia.codigoVerificacao();
    }

    private GuiaArrecadacao buscarPendente(UUID guiaId) {
        GuiaArrecadacao guia = guiaArrecadacaoRepository.buscarPorId(guiaId)
            .orElseThrow(() -> new NotFoundException("Guia de arrecadação não encontrada."));
        if (guia.situacao() != SituacaoGuia.PENDENTE) {
            throw new ValidationException("A guia não está pendente de pagamento.");
        }
        return guia;
    }

    private GuiaArrecadacao copiarGuia(
        GuiaArrecadacao guia,
        SituacaoGuia situacao,
        UUID formaPagamentoId,
        String codigoBarras,
        String pixTxid,
        StatusPix statusPix,
        String pixQrcodePayload,
        String pixLink,
        String pixEndToEndId,
        Instant pixSolicitadoEm
    ) {
        return new GuiaArrecadacao(
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
            situacao,
            formaPagamentoId,
            guia.dataEfetivacao(),
            guia.valorPago(),
            codigoBarras,
            pixTxid,
            guia.descricaoAvulsa(),
            guia.codigoVerificacao(),
            statusPix,
            pixQrcodePayload,
            pixLink,
            pixEndToEndId,
            pixSolicitadoEm
        );
    }
}
