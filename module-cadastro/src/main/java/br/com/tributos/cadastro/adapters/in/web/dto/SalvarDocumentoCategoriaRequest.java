package br.com.tributos.cadastro.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record SalvarDocumentoCategoriaRequest(
    @NotBlank String nome
) {
}
