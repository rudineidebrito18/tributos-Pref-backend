package br.com.tributos.iss.domain;

import java.util.UUID;

public record TipoSolicitacao(
    UUID id,
    UUID tenantId,
    String descricao,
    UUID usuarioNotificarId,
    boolean ativo
) {
}
