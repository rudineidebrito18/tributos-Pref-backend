package br.com.tributos.iptu.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record HabiteseResponsavelRequest(
    @NotBlank(message = "Informe o nome do responsável técnico.")
    String nome,
    String profissao,
    String documento
) {
}
