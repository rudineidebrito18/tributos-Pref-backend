package br.com.tributos.financeiro.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.financeiro.domain.FormaPagamentoRepository;
import br.com.tributos.financeiro.domain.GuiaArrecadacao;
import br.com.tributos.financeiro.domain.GuiaArrecadacaoRepository;
import br.com.tributos.financeiro.domain.SituacaoGuia;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;

@Service
public class RegistrarPagamentoService {

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
    public GuiaArrecadacao baixaManual(UUID guiaId, BigDecimal valorPago, String formaPagamentoCodigo) {
        GuiaArrecadacao guia = buscarPendente(guiaId);
        var forma = formaPagamentoRepository.buscarPorCodigo(formaPagamentoCodigo)
            .orElseThrow(() -> new ValidationException("Forma de pagamento inválida."));
        return efetivarPagamento(guia, valorPago, forma.id(), null, null);
    }

    @Transactional
    public SimulacaoPixResult simularPix(UUID guiaId) {
        GuiaArrecadacao guia = buscarPendente(guiaId);
        String txid = "MOCK-" + guia.id().toString().replace("-", "").substring(0, 20);
        String codigoBarras = "23793.38128 60000.000003 00000.000400 1 " + String.format("%014d", guia.numero());

        GuiaArrecadacao atualizada = efetivarParcialPix(guia, txid, codigoBarras);
        return new SimulacaoPixResult(
            atualizada.pixTxid(),
            atualizada.codigoBarras(),
            "00020126580014br.gov.bcb.pix0136" + txid + "5204000053039865802BR5925MOCK TRIBUTOS6009SAO PAULO62070503***6304ABCD"
        );
    }

    @Transactional
    public GuiaArrecadacao confirmarPix(UUID guiaId) {
        GuiaArrecadacao guia = guiaArrecadacaoRepository.buscarPorId(guiaId)
            .orElseThrow(() -> new NotFoundException("Guia de arrecadação não encontrada."));
        if (guia.situacao() == SituacaoGuia.PAGA) {
            return guia;
        }
        if (guia.pixTxid() == null) {
            throw new ValidationException("Simule o PIX antes de confirmar o pagamento.");
        }
        var formaPix = formaPagamentoRepository.buscarPorCodigo("PIX")
            .orElseThrow(() -> new IllegalStateException("Forma de pagamento PIX não configurada."));
        return efetivarPagamento(guia, guia.valor(), formaPix.id(), guia.pixTxid(), guia.codigoBarras());
    }

    private GuiaArrecadacao buscarPendente(UUID guiaId) {
        GuiaArrecadacao guia = guiaArrecadacaoRepository.buscarPorId(guiaId)
            .orElseThrow(() -> new NotFoundException("Guia de arrecadação não encontrada."));
        if (guia.situacao() != SituacaoGuia.PENDENTE) {
            throw new ValidationException("A guia não está pendente de pagamento.");
        }
        return guia;
    }

    private GuiaArrecadacao efetivarParcialPix(GuiaArrecadacao guia, String txid, String codigoBarras) {
        GuiaArrecadacao parcial = new GuiaArrecadacao(
            guia.id(), guia.tenantId(), guia.numero(), guia.tipoTributo(), guia.origemTipo(), guia.origemId(),
            guia.contribuinteId(), guia.competenciaMes(), guia.competenciaAno(), guia.dataEmissao(),
            guia.dataVencimento(), guia.valor(), guia.situacao(), guia.formaPagamentoId(), guia.dataEfetivacao(),
            guia.valorPago(), codigoBarras, txid, guia.descricaoAvulsa()
        );
        return guiaArrecadacaoRepository.salvar(parcial);
    }

    private GuiaArrecadacao efetivarPagamento(
        GuiaArrecadacao guia,
        BigDecimal valorPago,
        UUID formaPagamentoId,
        String pixTxid,
        String codigoBarras
    ) {
        if (valorPago == null || valorPago.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Valor pago inválido.");
        }
        GuiaArrecadacao paga = new GuiaArrecadacao(
            guia.id(), guia.tenantId(), guia.numero(), guia.tipoTributo(), guia.origemTipo(), guia.origemId(),
            guia.contribuinteId(), guia.competenciaMes(), guia.competenciaAno(), guia.dataEmissao(),
            guia.dataVencimento(), guia.valor(), SituacaoGuia.PAGA, formaPagamentoId, Instant.now(),
            valorPago, codigoBarras != null ? codigoBarras : guia.codigoBarras(),
            pixTxid != null ? pixTxid : guia.pixTxid(), guia.descricaoAvulsa()
        );
        return guiaArrecadacaoRepository.salvar(paga);
    }

    public record SimulacaoPixResult(String pixTxid, String codigoBarras, String qrCodePayload) {
    }
}
