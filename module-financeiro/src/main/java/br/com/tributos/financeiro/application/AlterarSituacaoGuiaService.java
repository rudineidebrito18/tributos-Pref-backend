package br.com.tributos.financeiro.application;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.financeiro.domain.GuiaArrecadacao;
import br.com.tributos.financeiro.domain.GuiaArrecadacaoRepository;
import br.com.tributos.financeiro.domain.SituacaoGuia;
import br.com.tributos.kernel.audit.AuditoriaPort;
import br.com.tributos.kernel.audit.RegistroAuditoria;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;

@Service
public class AlterarSituacaoGuiaService {

    private final GuiaArrecadacaoRepository guiaArrecadacaoRepository;
    private final AuditoriaPort auditoriaPort;

    public AlterarSituacaoGuiaService(
        GuiaArrecadacaoRepository guiaArrecadacaoRepository,
        AuditoriaPort auditoriaPort
    ) {
        this.guiaArrecadacaoRepository = guiaArrecadacaoRepository;
        this.auditoriaPort = auditoriaPort;
    }

    @Transactional
    public GuiaArrecadacao isentar(UUID guiaId) {
        GuiaArrecadacao guia = buscarPendente(guiaId);
        GuiaArrecadacao isenta = copiarComSituacao(guia, SituacaoGuia.ISENTA, null, null);
        GuiaArrecadacao salva = guiaArrecadacaoRepository.salvar(isenta);
        auditoriaPort.registrar(new RegistroAuditoria(
            "guia_arrecadacao",
            guiaId.toString(),
            "ISENTAR",
            resumo(guia),
            resumo(salva)
        ));
        return salva;
    }

    @Transactional
    public GuiaArrecadacao cancelar(UUID guiaId) {
        GuiaArrecadacao guia = buscarPendente(guiaId);
        GuiaArrecadacao cancelada = copiarComSituacao(guia, SituacaoGuia.CANCELADA, null, null);
        GuiaArrecadacao salva = guiaArrecadacaoRepository.salvar(cancelada);
        auditoriaPort.registrar(new RegistroAuditoria(
            "guia_arrecadacao",
            guiaId.toString(),
            "CANCELAR",
            resumo(guia),
            resumo(salva)
        ));
        return salva;
    }

    private GuiaArrecadacao buscarPendente(UUID guiaId) {
        GuiaArrecadacao guia = guiaArrecadacaoRepository.buscarPorId(guiaId)
            .orElseThrow(() -> new NotFoundException("Guia de arrecadação não encontrada."));
        if (guia.situacao() != SituacaoGuia.PENDENTE) {
            throw new ValidationException("A guia não está pendente.");
        }
        return guia;
    }

    private static GuiaArrecadacao copiarComSituacao(
        GuiaArrecadacao guia,
        SituacaoGuia situacao,
        UUID formaPagamentoId,
        BigDecimal valorPago
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
            null,
            valorPago,
            guia.codigoBarras(),
            guia.pixTxid(),
            guia.descricaoAvulsa(),
            guia.codigoVerificacao(),
            guia.tipoTributacao(),
            guia.statusPix(),
            guia.pixQrcodePayload(),
            guia.pixLink(),
            guia.pixEndToEndId(),
            guia.pixSolicitadoEm()
        );
    }

    private static Object resumo(GuiaArrecadacao g) {
        return java.util.Map.of(
            "situacao", g.situacao().name(),
            "valorPago", g.valorPago() != null ? g.valorPago() : BigDecimal.ZERO
        );
    }
}
