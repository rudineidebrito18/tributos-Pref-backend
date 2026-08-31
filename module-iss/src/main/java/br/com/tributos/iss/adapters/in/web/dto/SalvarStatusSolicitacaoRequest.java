package br.com.tributos.iss.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record SalvarStatusSolicitacaoRequest(
    @NotBlank(message = "Informe a descrição.")
    String descricao,
    boolean ativo
) {
}
