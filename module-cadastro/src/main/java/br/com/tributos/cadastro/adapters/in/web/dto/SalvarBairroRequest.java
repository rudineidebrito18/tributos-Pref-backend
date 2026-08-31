package br.com.tributos.cadastro.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SalvarBairroRequest(
    @NotNull UUID cidadeId,
    @NotBlank @Size(max = 200) String nome,
    UUID zonaFiscalId,
    BigDecimal valorTerreno
) {
}
