package br.com.tributos.iss.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record SalvarAtividadeRequest(
    @NotBlank(message = "Informe o código da atividade.")
    String codigo,
    @NotBlank(message = "Informe a descrição da atividade.")
    String descricao,
    boolean ativo
) {
}
