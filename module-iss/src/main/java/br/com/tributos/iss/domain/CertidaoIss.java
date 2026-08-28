package br.com.tributos.iss.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record CertidaoIss(
    UUID id,
    UUID tenantId,
    TipoCertidaoIss tipo,
    UUID contribuinteId,
    long numero,
    String codigoVerificacao,
    Instant dataEmissao,
    LocalDate validade
) {

    public boolean vigente(LocalDate referencia) {
        return ValidadorVigenciaDocumento.estaVigente(validade, referencia);
    }
}
