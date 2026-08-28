package br.com.tributos.iss.adapters.in.web.dto;

import java.math.BigDecimal;

import br.com.tributos.iss.domain.CalculadorAliquotaSimplesNacional;

public record CalcularAliquotaResponse(
    String anexoSimples,
    BigDecimal faixaReceitaMin,
    BigDecimal faixaReceitaMax,
    BigDecimal aliquotaNominal,
    BigDecimal aliquotaEfetivaSimples,
    BigDecimal aliquotaIssEfetiva
) {

    public static CalcularAliquotaResponse de(CalculadorAliquotaSimplesNacional.Resultado resultado) {
        return new CalcularAliquotaResponse(
            resultado.anexoSimples(),
            resultado.faixaReceitaMin(),
            resultado.faixaReceitaMax(),
            resultado.aliquotaNominal(),
            resultado.aliquotaEfetivaSimples(),
            resultado.aliquotaIssEfetiva()
        );
    }
}
