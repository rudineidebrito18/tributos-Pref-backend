package br.com.tributos.iss.adapters.in.web.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record AlterarStatusSolicitacaoRequest(
    @NotNull(message = "Informe o novo status.")
    UUID statusSolicitacaoId
) {
}
