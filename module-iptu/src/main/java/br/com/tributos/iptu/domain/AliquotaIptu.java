package br.com.tributos.iptu.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record AliquotaIptu(
    UUID id,
    UUID tenantId,
    int exercicio,
    UUID destinacaoId,
    UUID zonaFiscalId,
    BigDecimal aliquota
) {
}
