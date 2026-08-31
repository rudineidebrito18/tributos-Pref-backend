package br.com.tributos.iss.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.tributos.iss.domain.AtividadeServicoRepository.AtividadeServicoView;

public record AtividadeServicoViewResponse(
    UUID id,
    String cnae,
    String codigo,
    String servico,
    BigDecimal aliquota,
    boolean tributavel,
    boolean deducao,
    boolean retencao,
    String incidencia
) {

    public static AtividadeServicoViewResponse de(AtividadeServicoView view) {
        return new AtividadeServicoViewResponse(
            view.id(),
            view.cnae(),
            view.codigo(),
            view.servico(),
            view.aliquota(),
            view.tributavel(),
            view.deducao(),
            view.retencao(),
            view.incidencia()
        );
    }
}
