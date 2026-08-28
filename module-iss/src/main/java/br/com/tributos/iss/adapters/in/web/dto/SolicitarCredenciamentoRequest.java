package br.com.tributos.iss.adapters.in.web.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record SolicitarCredenciamentoRequest(
    @NotNull(message = "Informe o contribuinte.")
    UUID contribuinteId
) {
}
