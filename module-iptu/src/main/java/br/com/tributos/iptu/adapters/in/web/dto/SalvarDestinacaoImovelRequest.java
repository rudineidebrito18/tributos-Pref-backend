package br.com.tributos.iptu.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SalvarDestinacaoImovelRequest(
    @NotBlank String nome,
    boolean ativo,
    @NotNull UUID tipoImovelId,
    @NotNull @DecimalMin("0") @DecimalMax("100") BigDecimal aliquotaIptu
) {
}
