package br.com.tributos.iss.adapters.in.web.dto;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

import br.com.tributos.iss.domain.TipoCertidaoIss;

public record EmitirCertidaoRequest(
    @NotNull(message = "Informe o contribuinte.")
    UUID contribuinteId,
    @NotNull(message = "Informe o tipo da certidão.")
    TipoCertidaoIss tipo,
    LocalDate validade
) {
}
