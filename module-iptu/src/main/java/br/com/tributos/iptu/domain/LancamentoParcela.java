package br.com.tributos.iptu.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record LancamentoParcela(
    UUID id,
    UUID tenantId,
    UUID lancamentoId,
    int numeroParcela,
    BigDecimal valor,
    LocalDate vencimento,
    StatusParcelaIptu status
) {
}
