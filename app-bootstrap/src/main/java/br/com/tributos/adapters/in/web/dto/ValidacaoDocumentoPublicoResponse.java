package br.com.tributos.adapters.in.web.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import br.com.tributos.application.portal.TipoDocumentoPublico;
import br.com.tributos.application.portal.ValidacaoDocumentoPublicoResult;

public record ValidacaoDocumentoPublicoResponse(
    TipoDocumentoPublico tipoDocumento,
    UUID id,
    long numero,
    String codigoVerificacao,
    Instant dataEmissao,
    LocalDate validade,
    boolean vigente,
    String detalheTipo
) {

    public static ValidacaoDocumentoPublicoResponse de(ValidacaoDocumentoPublicoResult resultado) {
        return new ValidacaoDocumentoPublicoResponse(
            resultado.tipoDocumento(),
            resultado.id(),
            resultado.numero(),
            resultado.codigoVerificacao(),
            resultado.dataEmissao(),
            resultado.validade(),
            resultado.vigente(),
            resultado.detalheTipo()
        );
    }
}
