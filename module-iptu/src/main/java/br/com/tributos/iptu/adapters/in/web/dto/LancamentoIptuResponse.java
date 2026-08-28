package br.com.tributos.iptu.adapters.in.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.tributos.iptu.domain.LancamentoIptu;
import br.com.tributos.iptu.domain.StatusLancamentoIptu;

public record LancamentoIptuResponse(
    UUID id,
    UUID imovelId,
    int exercicio,
    BigDecimal valorVenalCalculado,
    BigDecimal aliquotaAplicada,
    BigDecimal valorTotal,
    int numeroParcelas,
    StatusLancamentoIptu status,
    Instant dataGeracao
) {

    public static LancamentoIptuResponse de(LancamentoIptu lancamento) {
        return new LancamentoIptuResponse(
            lancamento.id(),
            lancamento.imovelId(),
            lancamento.exercicio(),
            lancamento.valorVenalCalculado(),
            lancamento.aliquotaAplicada(),
            lancamento.valorTotal(),
            lancamento.numeroParcelas(),
            lancamento.status(),
            lancamento.dataGeracao()
        );
    }
}
