package br.com.tributos.iss.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CertidaoIss(
    UUID id,
    UUID tenantId,
    TipoCertidaoIss tipo,
    UUID contribuinteId,
    long numero,
    String codigoVerificacao,
    Instant dataEmissao,
    LocalDate validade,
    UUID situacaoCndId,
    String observacao,
    boolean avulsa,
    List<TributoCertidao> tributos
) {

    public boolean vigente(LocalDate referencia) {
        return ValidadorVigenciaDocumento.estaVigente(validade, referencia);
    }
}
