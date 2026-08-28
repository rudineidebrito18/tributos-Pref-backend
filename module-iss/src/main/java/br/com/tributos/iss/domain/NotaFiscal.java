package br.com.tributos.iss.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record NotaFiscal(
    UUID id,
    UUID tenantId,
    long numero,
    String serie,
    UUID contribuinteId,
    UUID tomadorId,
    UUID servicoId,
    LocalDate competencia,
    BigDecimal valorServico,
    BigDecimal valorDeducoes,
    BigDecimal baseCalculo,
    BigDecimal aliquotaAplicada,
    BigDecimal valorIss,
    StatusNotaFiscal status,
    UUID notaSubstitutaId,
    String motivoCancelamento,
    Instant dataEmissao
) {
}
