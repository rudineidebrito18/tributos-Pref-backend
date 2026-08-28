package br.com.tributos.iss.domain;

import java.util.UUID;

public record ContribuinteAtividadeServico(
    UUID id,
    UUID tenantId,
    UUID contribuinteId,
    UUID atividadeId,
    UUID servicoId,
    boolean tributavel
) {
}
