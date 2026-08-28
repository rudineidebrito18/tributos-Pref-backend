package br.com.tributos.iptu.adapters.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import br.com.tributos.iptu.application.BuscarLancamentoService.LancamentoComParcelas;
import br.com.tributos.iptu.domain.LancamentoParcela;
import br.com.tributos.iptu.domain.StatusParcelaIptu;

public record LancamentoIptuDetalheResponse(
    UUID id,
    UUID imovelId,
    int exercicio,
    BigDecimal valorVenalCalculado,
    BigDecimal aliquotaAplicada,
    BigDecimal valorTotal,
    int numeroParcelas,
    String status,
    List<LancamentoParcelaResponse> parcelas
) {

    public record LancamentoParcelaResponse(
        UUID id,
        int numeroParcela,
        BigDecimal valor,
        LocalDate vencimento,
        StatusParcelaIptu status
    ) {
    }

    public static LancamentoIptuDetalheResponse de(LancamentoComParcelas detalhe) {
        return new LancamentoIptuDetalheResponse(
            detalhe.lancamento().id(),
            detalhe.lancamento().imovelId(),
            detalhe.lancamento().exercicio(),
            detalhe.lancamento().valorVenalCalculado(),
            detalhe.lancamento().aliquotaAplicada(),
            detalhe.lancamento().valorTotal(),
            detalhe.lancamento().numeroParcelas(),
            detalhe.lancamento().status().name(),
            detalhe.parcelas().stream().map(LancamentoIptuDetalheResponse::deParcela).toList()
        );
    }

    private static LancamentoParcelaResponse deParcela(LancamentoParcela parcela) {
        return new LancamentoParcelaResponse(
            parcela.id(),
            parcela.numeroParcela(),
            parcela.valor(),
            parcela.vencimento(),
            parcela.status()
        );
    }
}
