package br.com.tributos.iss.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record NotaFiscalEmitidaEvent(
    UUID notaId,
    UUID tenantId,
    UUID contribuinteId,
    BigDecimal valorIss,
    Instant dataEmissao
) {
}
