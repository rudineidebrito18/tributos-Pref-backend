package br.com.tributos.iss.domain;

import java.util.UUID;

public record LocalIncidencia(
    UUID id,
    UUID tenantId,
    String descricao,
    boolean ativo
) {
}
