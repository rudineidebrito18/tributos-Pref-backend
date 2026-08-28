package br.com.tributos.identity.adapters.in.web.dto;

import java.util.UUID;

import br.com.tributos.identity.application.TenantCriado;

/**
 * {@code senhaTemporaria} só aparece nesta resposta, uma única vez — depois disso não há
 * como recuperá-la (só o hash BCrypt fica salvo). A tela de onboarding deve exibi-la ao
 * operador da plataforma e orientar a troca no primeiro login do tenant.
 */
public record TenantCriadoResponse(
    UUID tenantId,
    String slug,
    UUID usuarioAdminId,
    String usuarioAdminLogin,
    String senhaTemporaria
) {

    public static TenantCriadoResponse de(TenantCriado resultado) {
        return new TenantCriadoResponse(
            resultado.tenantId(),
            resultado.slug(),
            resultado.usuarioAdminId(),
            resultado.usuarioAdminLogin(),
            resultado.senhaTemporaria()
        );
    }
}
