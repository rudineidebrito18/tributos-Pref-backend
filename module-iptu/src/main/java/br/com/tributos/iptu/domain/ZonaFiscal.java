package br.com.tributos.iptu.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record ZonaFiscal(
    UUID id,
    UUID tenantId,
    String nome,
    BigDecimal fatorValorizacao,
    boolean ativo
) {
}
