package br.com.tributos.iss.domain;

import java.util.UUID;

public record GrupoServico(
    UUID id,
    UUID tenantId,
    String codigo,
    String descricao
) {
}
