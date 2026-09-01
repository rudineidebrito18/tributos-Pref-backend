package br.com.tributos.iptu.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CriarImovelObservacaoRequest(
    @NotBlank(message = "Informe o texto da observação.")
    String texto
) {
}
