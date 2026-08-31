package br.com.tributos.iss.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.tributos.iss.domain.AtividadeServico;

public record AtividadeServicoResponse(
    UUID id,
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

    public static AtividadeServicoResponse de(AtividadeServico atividadeServico) {
        return new AtividadeServicoResponse(
            atividadeServico.id(),
            atividadeServico.atividadeId(),
            atividadeServico.servicoId(),
            atividadeServico.localIncidenciaId(),
            atividadeServico.aliquota(),
            atividadeServico.tributavel(),
            atividadeServico.imune(),
            atividadeServico.deducao(),
            atividadeServico.substitutoTributario(),
            atividadeServico.retencaoFonte(),
            atividadeServico.regimeEspecial(),
            atividadeServico.observacao()
        );
    }
}
