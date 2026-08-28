package br.com.tributos.iss.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record TipoAlvara(
    UUID id,
    UUID tenantId,
    String nome,
    BigDecimal valorBase,
    int diasValidade,
    boolean ativo
) {
}
