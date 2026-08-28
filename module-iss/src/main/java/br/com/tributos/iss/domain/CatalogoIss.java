package br.com.tributos.iss.domain;

import java.util.UUID;

public record CatalogoIss(UUID id, UUID tenantId, String nome, boolean ativo) {
}
