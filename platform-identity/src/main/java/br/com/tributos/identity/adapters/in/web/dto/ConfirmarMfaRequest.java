package br.com.tributos.identity.adapters.in.web.dto;

import jakarta.validation.constraints.Pattern;

public record ConfirmarMfaRequest(
    @Pattern(regexp = "\\d{6}", message = "Código deve ter 6 dígitos.") String codigo
) {
}
