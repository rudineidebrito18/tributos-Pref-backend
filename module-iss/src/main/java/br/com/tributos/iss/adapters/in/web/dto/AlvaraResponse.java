package br.com.tributos.iss.adapters.in.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import br.com.tributos.iss.domain.Alvara;
import br.com.tributos.iss.domain.SituacaoFiscalAlvara;

public record AlvaraResponse(
    UUID id,
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

    public static AlvaraResponse de(Alvara alvara) {
        return new AlvaraResponse(
            alvara.id(),
            alvara.numero(),
            alvara.tipoAlvaraId(),
            alvara.contribuinteId(),
            alvara.dataExpedicao(),
            alvara.validade(),
            alvara.situacaoFiscal(),
            alvara.valor(),
            alvara.codigoVerificacao(),
            alvara.dataEmissao()
        );
    }
}
