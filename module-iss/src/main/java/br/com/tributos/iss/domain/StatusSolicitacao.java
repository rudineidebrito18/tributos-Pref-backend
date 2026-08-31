package br.com.tributos.iss.domain;

import java.util.UUID;

public record StatusSolicitacao(
    UUID id,
    UUID tenantId,
    String descricao,
    boolean ativo
) {
}
