package br.com.tributos.financeiro.adapters.in.web.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record BaixaManualLoteRequest(
    @NotEmpty List<UUID> guiaIds,
    @NotNull String formaPagamentoCodigo,
    @NotNull Instant dataEfetivacao
) {
}
