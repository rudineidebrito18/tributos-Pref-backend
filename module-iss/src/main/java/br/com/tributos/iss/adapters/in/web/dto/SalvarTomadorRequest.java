package br.com.tributos.iss.adapters.in.web.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record SalvarTomadorRequest(
    @NotNull(message = "Informe a pessoa vinculada ao tomador.")
    UUID pessoaId
) {
}
