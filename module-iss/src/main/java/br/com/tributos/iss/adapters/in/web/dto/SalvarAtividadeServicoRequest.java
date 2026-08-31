package br.com.tributos.iss.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record SalvarAtividadeServicoRequest(
    @NotNull(message = "Informe a atividade.")
    UUID atividadeId,
    UUID servicoId,
    @NotNull(message = "Informe o local de incidência.")
    UUID localIncidenciaId,
    @NotNull(message = "Informe a alíquota.")
    BigDecimal aliquota,
    boolean tributavel,
    boolean imune,
    boolean deducao,
    boolean substitutoTributario,
    boolean retencaoFonte,
    String regimeEspecial,
    String observacao
) {
}
