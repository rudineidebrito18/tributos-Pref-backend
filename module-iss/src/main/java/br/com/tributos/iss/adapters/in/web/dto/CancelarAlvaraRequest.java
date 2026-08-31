package br.com.tributos.iss.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CancelarAlvaraRequest(
    @NotBlank(message = "Informe o motivo do cancelamento.")
    String motivoCancelamento
) {
}
