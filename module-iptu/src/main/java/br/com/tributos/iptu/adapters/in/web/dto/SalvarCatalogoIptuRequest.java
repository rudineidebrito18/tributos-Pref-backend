package br.com.tributos.iptu.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record SalvarCatalogoIptuRequest(
    @NotBlank(message = "Informe o nome.")
    String nome,
    boolean ativo
) {
}
