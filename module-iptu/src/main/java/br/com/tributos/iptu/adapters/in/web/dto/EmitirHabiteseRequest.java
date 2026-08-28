package br.com.tributos.iptu.adapters.in.web.dto;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record EmitirHabiteseRequest(
    @NotNull(message = "Informe o tipo do habite-se.")
    UUID tipoId,
    @NotNull(message = "Informe a data de emissão.")
    LocalDate dataEmissao
) {
}
