package br.com.tributos.iss.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record SalvarSituacaoCndRequest(
    @NotBlank(message = "Informe a descrição.")
    String descricao,
    @NotBlank(message = "Informe o título.")
    String titulo,
    boolean ativo
) {
}
