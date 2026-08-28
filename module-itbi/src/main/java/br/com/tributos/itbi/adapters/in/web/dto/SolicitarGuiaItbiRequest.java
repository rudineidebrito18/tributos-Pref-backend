package br.com.tributos.itbi.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SolicitarGuiaItbiRequest(
    @NotNull UUID imovelId,
    @NotNull UUID adquirenteId,
    @NotNull UUID tipoGuiaId,
    @NotNull UUID naturezaTransmissaoId,
    @NotNull @Positive BigDecimal valorTransacao
) {
}
