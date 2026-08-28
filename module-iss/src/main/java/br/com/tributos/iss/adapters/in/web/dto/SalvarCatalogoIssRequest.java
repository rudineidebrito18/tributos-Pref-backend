package br.com.tributos.iss.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record SalvarCatalogoIssRequest(
    @NotBlank(message = "Informe o nome.")
    String nome,
    boolean ativo
) {
}
