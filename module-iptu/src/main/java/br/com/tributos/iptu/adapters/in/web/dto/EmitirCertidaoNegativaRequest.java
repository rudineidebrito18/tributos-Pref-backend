package br.com.tributos.iptu.adapters.in.web.dto;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record EmitirCertidaoNegativaRequest(
    LocalDate validade,
    @NotNull(message = "Informe a situação da certidão negativa.")
    UUID situacaoCndId,
    String observacao
) {
}
