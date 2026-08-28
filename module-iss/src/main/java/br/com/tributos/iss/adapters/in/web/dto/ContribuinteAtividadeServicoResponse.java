package br.com.tributos.iss.adapters.in.web.dto;

import java.util.UUID;

import br.com.tributos.iss.domain.ContribuinteAtividadeServico;

public record ContribuinteAtividadeServicoResponse(
    UUID id,
    UUID contribuinteId,
    UUID atividadeId,
    UUID servicoId,
    boolean tributavel
) {

    public static ContribuinteAtividadeServicoResponse de(ContribuinteAtividadeServico vinculo) {
        return new ContribuinteAtividadeServicoResponse(
            vinculo.id(),
            vinculo.contribuinteId(),
            vinculo.atividadeId(),
            vinculo.servicoId(),
            vinculo.tributavel()
        );
    }
}
