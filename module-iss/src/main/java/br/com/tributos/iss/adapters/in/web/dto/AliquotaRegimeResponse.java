package br.com.tributos.iss.adapters.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import br.com.tributos.iss.domain.AliquotaRegime;

public record AliquotaRegimeResponse(
    UUID id,
    UUID regimeId,
    BigDecimal faixaReceitaMin,
    BigDecimal faixaReceitaMax,
    BigDecimal aliquotaNominal,
    BigDecimal parcelaDeduzir,
    BigDecimal percentualIss,
    LocalDate competenciaVigencia,
    String anexoSimples
) {

    public static AliquotaRegimeResponse de(AliquotaRegime faixa) {
        return new AliquotaRegimeResponse(
            faixa.id(),
            faixa.regimeId(),
            faixa.faixaReceitaMin(),
            faixa.faixaReceitaMax(),
            faixa.aliquotaNominal(),
            faixa.parcelaDeduzir(),
            faixa.percentualIss(),
            faixa.competenciaVigencia(),
            faixa.anexoSimples()
        );
    }
}
