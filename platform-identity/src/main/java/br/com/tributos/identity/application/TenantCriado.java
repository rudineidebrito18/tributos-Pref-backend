package br.com.tributos.identity.application;

import java.util.UUID;

/**
 * Resultado do onboarding administrativo — {@code senhaTemporaria} só existe aqui, em
 * memória, entre {@link CriarTenantService#executar} e a resposta HTTP; nunca é
 * persistida em texto puro nem logada.
 */
public record TenantCriado(
    UUID tenantId,
    String slug,
    UUID usuarioAdminId,
    String usuarioAdminLogin,
    String senhaTemporaria
) {
}
