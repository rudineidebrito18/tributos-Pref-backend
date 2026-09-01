package br.com.tributos.financeiro.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.financeiro.domain.FormaPagamentoRepository;
import br.com.tributos.financeiro.domain.GuiaArrecadacao;
import br.com.tributos.financeiro.domain.GuiaArrecadacaoRepository;
import br.com.tributos.financeiro.domain.SituacaoGuia;
import br.com.tributos.financeiro.domain.StatusPix;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;

@Service
public class RegistrarPagamentoService {

    static final String CODIGO_FORMA_BAIXA_MANUAL = "BAIXA_MANUAL";
    static final String CODIGO_FORMA_PIX = "PIX";

    private final GuiaArrecadacaoRepository guiaArrecadacaoRepository;
    private final FormaPagamentoRepository formaPagamentoRepository;

    public RegistrarPagamentoService(
        GuiaArrecadacaoRepository guiaArrecadacaoRepository,
        FormaPagamentoRepository formaPagamentoRepository
    ) {
        this.guiaArrecadacaoRepository = guiaArrecadacaoRepository;
        this.formaPagamentoRepository = formaPagamentoRepository;
    }

    @Transactional
    public GuiaArrecadacao baixaManual(UUID guiaId, BigDecimal valorPago) {
        return baixaManual(guiaId, valorPago, CODIGO_FORMA_BAIXA_MANUAL, Instant.now());
    }

    @Transactional
    public GuiaArrecadacao baixaManual(UUID guiaId, BigDecimal valorPago, String formaPagamentoCodigo, Instant dataEfetivacao) {
        GuiaArrecadacao guia = buscarPendente(guiaId);
        var forma = formaPagamentoRepository.buscarPorCodigo(formaPagamentoCodigo)
            .orElseThrow(() -> new ValidationException("Forma de pagamento não encontrada: " + formaPagamentoCodigo));
        return efetivarPagamento(
            guia,
            valorPago,
            forma.id(),
            null,
            null,
            StatusPix.ATUALIZACAO_MANUAL,
            dataEfetivacao != null ? dataEfetivacao : Instant.now()
        );
    }

    @Transactional
    public List<GuiaArrecadacao> baixaManualLote(List<UUID> guiaIds, String formaPagamentoCodigo, Instant dataEfetivacao) {
        if (guiaIds == null || guiaIds.isEmpty()) {
            throw new ValidationException("Informe ao menos uma guia para baixa.");
        }
        var forma = formaPagamentoRepository.buscarPorCodigo(formaPagamentoCodigo)
            .orElseThrow(() -> new ValidationException("Forma de pagamento não encontrada: " + formaPagamentoCodigo));
        Instant efetivacao = dataEfetivacao != null ? dataEfetivacao : Instant.now();
        List<GuiaArrecadacao> resultados = new ArrayList<>();
        for (UUID guiaId : guiaIds) {
            GuiaArrecadacao guia = buscarPendente(guiaId);
            resultados.add(efetivarPagamento(
                guia,
                guia.valor(),
                forma.id(),
                null,
                null,
                StatusPix.ATUALIZACAO_MANUAL,
                efetivacao
            ));
        }
        return resultados;
    }

    @Transactional
    public GuiaArrecadacao confirmarPix(UUID guiaId) {
        GuiaArrecadacao guia = guiaArrecadacaoRepository.buscarPorId(guiaId)
            .orElseThrow(() -> new NotFoundException("Guia de arrecadação não encontrada."));
        if (guia.situacao() == SituacaoGuia.PAGA) {
            return guia;
        }
        if (guia.pixTxid() == null) {
            throw new ValidationException("Gere o PIX antes de confirmar o pagamento.");
        }
        var formaPix = formaPagamentoRepository.buscarPorCodigo(CODIGO_FORMA_PIX)
            .orElseThrow(() -> new IllegalStateException("Forma de pagamento PIX não configurada."));
        return efetivarPagamento(
            guia,
            guia.valor(),
            formaPix.id(),
            guia.pixTxid(),
            guia.codigoBarras(),
            StatusPix.CONCLUIDA,
            Instant.now()
        );
    }

    @Transactional
    public ResultadoLiquidacaoPix liquidacaoViaWebhook(
        GuiaArrecadacao guia,
        BigDecimal valorRecebido,
        String endToEndId,
        Instant horarioPagamento
    ) {
        if (valorRecebido == null || valorRecebido.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Valor recebido inválido.");
        }
        if (guia.situacao() == SituacaoGuia.PAGA
            && endToEndId != null
            && endToEndId.equals(guia.pixEndToEndId())) {
            return new ResultadoLiquidacaoPix(guia, true, true);
        }
        var formaPix = formaPagamentoRepository.buscarPorCodigo(CODIGO_FORMA_PIX)
            .orElseThrow(() -> new IllegalStateException("Forma de pagamento PIX não configurada."));

        boolean valorCompleto = valorRecebido.compareTo(guia.valor()) >= 0;
        SituacaoGuia novaSituacao = valorCompleto ? SituacaoGuia.PAGA : SituacaoGuia.PENDENTE;
        Instant dataEfetivacao = valorCompleto
            ? (horarioPagamento != null ? horarioPagamento : Instant.now())
            : guia.dataEfetivacao();

        GuiaArrecadacao atualizada = copiarGuia(
            guia,
            novaSituacao,
            formaPix.id(),
            dataEfetivacao,
            valorRecebido,
            guia.codigoBarras(),
            guia.pixTxid(),
            StatusPix.CONCLUIDA,
            guia.pixQrcodePayload(),
            guia.pixLink(),
            endToEndId,
            guia.pixSolicitadoEm()
        );
        return new ResultadoLiquidacaoPix(guiaArrecadacaoRepository.salvar(atualizada), valorCompleto, false);
    }

    private GuiaArrecadacao buscarPendente(UUID guiaId) {
        GuiaArrecadacao guia = guiaArrecadacaoRepository.buscarPorId(guiaId)
            .orElseThrow(() -> new NotFoundException("Guia de arrecadação não encontrada."));
        if (guia.situacao() != SituacaoGuia.PENDENTE) {
            throw new ValidationException("A guia não está pendente de pagamento.");
        }
        return guia;
    }

    private GuiaArrecadacao efetivarPagamento(
        GuiaArrecadacao guia,
        BigDecimal valorPago,
        UUID formaPagamentoId,
        String pixTxid,
        String codigoBarras,
        StatusPix statusPix,
        Instant dataEfetivacao
    ) {
        if (valorPago == null || valorPago.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Valor pago inválido.");
        }
        GuiaArrecadacao paga = copiarGuia(
            guia,
            SituacaoGuia.PAGA,
            formaPagamentoId,
            dataEfetivacao,
            valorPago,
            codigoBarras != null ? codigoBarras : guia.codigoBarras(),
            pixTxid != null ? pixTxid : guia.pixTxid(),
            statusPix,
            guia.pixQrcodePayload(),
            guia.pixLink(),
            guia.pixEndToEndId(),
            guia.pixSolicitadoEm()
        );
        return guiaArrecadacaoRepository.salvar(paga);
    }

    private GuiaArrecadacao efetivarPagamento(
        GuiaArrecadacao guia,
        BigDecimal valorPago,
        UUID formaPagamentoId,
        String pixTxid,
        String codigoBarras,
        StatusPix statusPix
    ) {
        return efetivarPagamento(guia, valorPago, formaPagamentoId, pixTxid, codigoBarras, statusPix, Instant.now());
    }

    private GuiaArrecadacao copiarGuia(
        GuiaArrecadacao guia,
        SituacaoGuia situacao,
        UUID formaPagamentoId,
        Instant dataEfetivacao,
        BigDecimal valorPago,
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
            dataEfetivacao,
            valorPago,
            codigoBarras,
            pixTxid,
            guia.descricaoAvulsa(),
            guia.codigoVerificacao(),
            guia.tipoTributacao(),
            statusPix,
            pixQrcodePayload,
            pixLink,
            pixEndToEndId,
            pixSolicitadoEm
        );
    }
}
