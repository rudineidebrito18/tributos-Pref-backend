package br.com.tributos.iss.adapters.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record CalcularAliquotaRequest(
    @NotNull(message = "Informe a receita bruta acumulada dos últimos 12 meses.")
    BigDecimal receitaBrutaAcumulada12Meses,
    LocalDate competencia
) {
}
