package br.com.tributos.cadastro.adapters.in.web.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record CompartilharDocumentoRequest(
    @NotNull UUID usuarioId
) {
}
