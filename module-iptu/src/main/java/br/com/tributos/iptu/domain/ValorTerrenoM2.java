package br.com.tributos.iptu.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record ValorTerrenoM2(
    UUID id,
    UUID tenantId,
    UUID zonaFiscalId,
    int exercicio,
    BigDecimal valorM2
) {
}
