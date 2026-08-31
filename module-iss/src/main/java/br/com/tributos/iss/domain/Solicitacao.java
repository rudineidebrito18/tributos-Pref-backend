package br.com.tributos.iss.domain;

import java.time.Instant;
import java.util.UUID;

public record Solicitacao(
    UUID id,
    UUID tenantId,
    UUID usuarioId,
    UUID tipoSolicitacaoId,
    UUID statusSolicitacaoId,
    String descricao,
    Instant dataHora,
    Instant criadoEm
) {
}
