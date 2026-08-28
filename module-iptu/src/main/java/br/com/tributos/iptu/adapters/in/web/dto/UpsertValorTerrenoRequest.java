package br.com.tributos.iptu.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record UpsertValorTerrenoRequest(
    @NotNull(message = "Informe a zona fiscal.")
    UUID zonaFiscalId,
    @NotNull(message = "Informe o valor do terreno por m².")
    BigDecimal valorM2
) {
}
