package br.com.tributos.iss.adapters.in.web.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import br.com.tributos.iss.domain.ResultadoValidacaoDocumento;
import br.com.tributos.iss.domain.TipoCertidaoIss;
import br.com.tributos.iss.domain.TipoDocumentoIss;

public record ValidacaoDocumentoResponse(
    TipoDocumentoIss tipoDocumento,
    UUID id,
    long numero,
    String codigoVerificacao,
    UUID contribuinteId,
    Instant dataEmissao,
    LocalDate validade,
    boolean vigente,
    TipoCertidaoIss tipoCertidao
) {

    public static ValidacaoDocumentoResponse de(ResultadoValidacaoDocumento resultado) {
        return new ValidacaoDocumentoResponse(
            resultado.tipoDocumento(),
            resultado.id(),
            resultado.numero(),
            resultado.codigoVerificacao(),
            resultado.contribuinteId(),
            resultado.dataEmissao(),
            resultado.validade(),
            resultado.vigente(),
            resultado.tipoCertidao().orElse(null)
        );
    }
}
