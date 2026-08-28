package br.com.tributos.iptu.adapters.in.web.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SalvarZonaFiscalRequest(
    @NotBlank(message = "Informe o nome da zona fiscal.")
    String nome,
    BigDecimal fatorValorizacao,
    @NotNull(message = "Informe se a zona fiscal está ativa.")
    Boolean ativo
) {
}
