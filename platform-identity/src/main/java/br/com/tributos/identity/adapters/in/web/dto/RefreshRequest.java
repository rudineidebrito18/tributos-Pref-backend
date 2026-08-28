package br.com.tributos.identity.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(@NotBlank(message = "refreshToken ausente.") String refreshToken) {
}
