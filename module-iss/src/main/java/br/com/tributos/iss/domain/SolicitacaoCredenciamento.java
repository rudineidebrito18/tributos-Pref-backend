package br.com.tributos.iss.domain;

import java.time.Instant;
import java.util.UUID;

public record SolicitacaoCredenciamento(
    UUID id,
    UUID tenantId,
    UUID contribuinteId,
    UUID statusId,
    String observacao,
    UUID analisadoPor,
    Instant analisadoEm,
    Instant criadoEm
) {
}
