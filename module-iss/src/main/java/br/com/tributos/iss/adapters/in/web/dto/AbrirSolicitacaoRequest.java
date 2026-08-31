package br.com.tributos.iss.adapters.in.web.dto;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AbrirSolicitacaoRequest(
    @NotNull(message = "Informe o tipo de solicitação.")
    UUID tipoSolicitacaoId,

    @NotNull(message = "Informe o status inicial.")
    UUID statusSolicitacaoId,

    @NotBlank(message = "Informe a descrição.")
    String descricao,

    @NotNull(message = "Informe a data/hora.")
    Instant dataHora
) {
}
