package br.com.tributos.iptu.domain;

import java.util.UUID;

public record CatalogoIptu(UUID id, UUID tenantId, String nome, boolean ativo) {
}
