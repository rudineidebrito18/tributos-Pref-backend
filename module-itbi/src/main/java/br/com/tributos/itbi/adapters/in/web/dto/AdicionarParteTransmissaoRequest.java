package br.com.tributos.itbi.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AdicionarParteTransmissaoRequest(
    @NotNull UUID contribuinteId,
    @NotNull @Positive BigDecimal porcentagem,
    boolean principal
) {
}
