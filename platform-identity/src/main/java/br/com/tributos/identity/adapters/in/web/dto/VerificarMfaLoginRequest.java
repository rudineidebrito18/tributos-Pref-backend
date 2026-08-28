package br.com.tributos.identity.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerificarMfaLoginRequest(
    @NotBlank(message = "Token de verificação MFA ausente.") String tokenMfaPendente,
    @Pattern(regexp = "\\d{6}", message = "Código deve ter 6 dígitos.") String codigo
) {
}
