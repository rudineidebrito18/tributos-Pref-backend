package br.com.tributos.iss.adapters.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record SalvarAliquotaRegimeRequest(
    @NotNull(message = "Informe a receita mínima da faixa.")
    BigDecimal faixaReceitaMin,
    BigDecimal faixaReceitaMax,
    @NotNull(message = "Informe a alíquota nominal da faixa.")
    BigDecimal aliquotaNominal,
    BigDecimal parcelaDeduzir,
    BigDecimal percentualIss,
    @NotNull(message = "Informe a competência de vigência da faixa.")
    LocalDate competenciaVigencia,
    String anexoSimples
) {
}
