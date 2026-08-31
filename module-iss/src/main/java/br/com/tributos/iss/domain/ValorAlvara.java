package br.com.tributos.iss.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ValorAlvara(
    UUID id,
    UUID tenantId,
    UUID tipoAlvaraId,
    short anoVigencia,
    BigDecimal valor,
    UUID usuarioId,
    Instant atualizadoEm
) {
}
