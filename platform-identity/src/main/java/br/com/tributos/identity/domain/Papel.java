package br.com.tributos.identity.domain;

import java.util.UUID;

/**
 * Papel RBAC. {@code tenantId == null} identifica um papel global, disponível a qualquer
 * tenant (ex.: {@code SUPORTE}, da equipe da própria plataforma) — ver
 * PLANEJAMENTO_PROJETO.md §9 para os papéis-base sugeridos
 * ({@code ADMIN_TENANT}, {@code FISCAL}, {@code ATENDENTE}, ...).
 */
public record Papel(UUID id, UUID tenantId, String nome, String descricao) {
}
