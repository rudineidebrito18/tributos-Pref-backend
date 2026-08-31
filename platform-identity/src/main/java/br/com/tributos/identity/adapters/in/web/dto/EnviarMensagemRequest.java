package br.com.tributos.identity.adapters.in.web.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record EnviarMensagemRequest(
    @NotBlank @Size(max = 200) String assunto,
    @NotBlank String corpo,
    @NotEmpty List<UUID> destinatarioIds
) {
}
