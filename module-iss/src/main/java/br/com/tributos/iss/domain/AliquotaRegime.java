package br.com.tributos.iss.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AliquotaRegime(
    UUID id,
    UUID tenantId,
    UUID regimeId,
    BigDecimal faixaReceitaMin,
    BigDecimal faixaReceitaMax,
    BigDecimal aliquotaNominal,
    BigDecimal parcelaDeduzir,
    BigDecimal percentualIss,
    LocalDate competenciaVigencia,
    String anexoSimples
) {

    public CalculadorAliquotaSimplesNacional.FaixaAliquota paraFaixa() {
        return new CalculadorAliquotaSimplesNacional.FaixaAliquota(
            faixaReceitaMin, faixaReceitaMax, aliquotaNominal, parcelaDeduzir, percentualIss, anexoSimples
        );
    }
}
