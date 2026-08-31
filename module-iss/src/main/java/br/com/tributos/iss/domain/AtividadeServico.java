package br.com.tributos.iss.domain;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.tributos.kernel.exception.RegraNegocioException;

public record AtividadeServico(
    UUID id,
    UUID tenantId,
    UUID atividadeId,
    UUID servicoId,
    UUID localIncidenciaId,
    BigDecimal aliquota,
    boolean tributavel,
    boolean imune,
    boolean deducao,
    boolean substitutoTributario,
    boolean retencaoFonte,
    String regimeEspecial,
    String observacao
) {

    public AtividadeServico {
        if (imune && aliquota != null && aliquota.compareTo(BigDecimal.ZERO) != 0) {
            throw new RegraNegocioException("Atividade imune deve ter alíquota zero.");
        }
    }
}
