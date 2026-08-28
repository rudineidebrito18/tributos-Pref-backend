package br.com.tributos.iss.adapters.in.web.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SalvarTipoAlvaraRequest(
    @NotBlank(message = "Informe o nome do tipo de alvará.")
    String nome,
    @NotNull(message = "Informe o valor base do tipo de alvará.")
    BigDecimal valorBase,
    @Positive(message = "Os dias de validade devem ser maiores que zero.")
    int diasValidade,
    boolean ativo
) {
}
