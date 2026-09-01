package br.com.tributos.application.portal;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ValidacaoDocumentoPublicoResult(
    TipoDocumentoPublico tipoDocumento,
    UUID id,
    long numero,
    String codigoVerificacao,
    Instant dataEmissao,
    LocalDate validade,
    boolean vigente,
    String detalheTipo
) {
}
