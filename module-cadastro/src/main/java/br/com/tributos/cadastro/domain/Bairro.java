package br.com.tributos.cadastro.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Bairro(
    UUID id,
    UUID tenantId,
    UUID cidadeId,
    String nome,
    UUID zonaFiscalId,
    BigDecimal valorTerreno,
    Instant criadoEm
) {
}
