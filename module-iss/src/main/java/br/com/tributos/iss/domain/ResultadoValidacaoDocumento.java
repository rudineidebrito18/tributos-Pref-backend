package br.com.tributos.iss.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public record ResultadoValidacaoDocumento(
    TipoDocumentoIss tipoDocumento,
    UUID id,
    long numero,
    String codigoVerificacao,
    UUID contribuinteId,
    Instant dataEmissao,
    LocalDate validade,
    boolean vigente,
    Optional<TipoCertidaoIss> tipoCertidao
) {
}
