package br.com.tributos.itbi.domain;

import java.util.UUID;

public record TipoCalculoGuiaItbi(UUID id, UUID tenantId, String descricao) {
}
