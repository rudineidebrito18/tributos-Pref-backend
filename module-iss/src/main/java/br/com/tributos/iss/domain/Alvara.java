package br.com.tributos.iss.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record Alvara(
    UUID id,
    UUID tenantId,
    long numero,
    UUID tipoAlvaraId,
    UUID contribuinteId,
    LocalDate dataExpedicao,
    LocalDate validade,
    SituacaoFiscalAlvara situacaoFiscal,
    BigDecimal valor,
    String codigoVerificacao,
    Instant dataEmissao
) {

    public boolean vigente(LocalDate referencia) {
        return ValidadorVigenciaDocumento.estaVigente(validade, referencia);
    }
}
