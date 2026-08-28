package br.com.tributos.iptu.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LancamentoIptu(
    UUID id,
    UUID tenantId,
    UUID imovelId,
    int exercicio,
    BigDecimal valorVenalCalculado,
    BigDecimal aliquotaAplicada,
    BigDecimal valorTotal,
    int numeroParcelas,
    StatusLancamentoIptu status,
    Instant dataGeracao
) {
}
