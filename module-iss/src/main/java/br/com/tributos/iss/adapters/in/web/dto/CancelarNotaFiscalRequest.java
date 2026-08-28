package br.com.tributos.iss.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CancelarNotaFiscalRequest(
    @NotBlank(message = "Informe o motivo do cancelamento.")
    String motivo
) {
}
