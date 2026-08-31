package br.com.tributos.iss.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record SalvarGrupoServicoRequest(
    @NotBlank(message = "Informe o código.")
    String codigo,
    @NotBlank(message = "Informe a descrição.")
    String descricao
) {
}
