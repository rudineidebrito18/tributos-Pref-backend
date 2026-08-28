package br.com.tributos.financeiro.adapters.in.web.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BaixaManualRequest(
    @NotNull @Positive BigDecimal valorPago,
    @NotBlank String formaPagamentoCodigo
) {
}
