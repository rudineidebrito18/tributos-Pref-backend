package br.com.tributos.iss.domain;

import java.util.UUID;

public record Atividade(UUID id, UUID tenantId, String codigo, String descricao, boolean ativo) {
}
