package br.com.tributos.iss.domain;

import java.util.UUID;

public record SituacaoCnd(
    UUID id,
    UUID tenantId,
    String descricao,
    String titulo,
    boolean ativo
) {
}
