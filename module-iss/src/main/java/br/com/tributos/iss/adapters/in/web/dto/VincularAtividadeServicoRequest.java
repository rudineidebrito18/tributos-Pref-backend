package br.com.tributos.iss.adapters.in.web.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record VincularAtividadeServicoRequest(
    @NotNull(message = "Informe a atividade.")
    UUID atividadeId,
    @NotNull(message = "Informe o serviço.")
    UUID servicoId,
    boolean tributavel
) {
}
