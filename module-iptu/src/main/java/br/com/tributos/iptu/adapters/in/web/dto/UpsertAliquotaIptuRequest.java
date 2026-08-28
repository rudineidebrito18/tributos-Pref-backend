package br.com.tributos.iptu.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record UpsertAliquotaIptuRequest(
    @NotNull(message = "Informe a destinação.")
    UUID destinacaoId,
    @NotNull(message = "Informe a zona fiscal.")
    UUID zonaFiscalId,
    @NotNull(message = "Informe a alíquota.")
    BigDecimal aliquota
) {
}
