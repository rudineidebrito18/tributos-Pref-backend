package br.com.tributos.iss.application;

import java.util.UUID;

public record SalvarAtividadeServicoComando(
    UUID atividadeId,
    UUID servicoId,
    UUID localIncidenciaId,
    java.math.BigDecimal aliquota,
    boolean tributavel,
    boolean imune,
    boolean deducao,
    boolean substitutoTributario,
    boolean retencaoFonte,
    String regimeEspecial,
    String observacao
) {
}
