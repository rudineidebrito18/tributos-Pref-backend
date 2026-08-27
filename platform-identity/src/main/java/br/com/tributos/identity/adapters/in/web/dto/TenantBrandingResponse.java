package br.com.tributos.identity.adapters.in.web.dto;

import java.util.Set;

import br.com.tributos.identity.domain.Tenant;

/**
 * Forma EXATAMENTE espelhada em {@code TenantBranding} do frontend
 * (frontend/src/lib/tenant/types.ts) — os dois lados precisam mudar juntos. Isto é o
 * contrato entre {@code GET /api/public/tenants/{slug}/branding} e
 * {@code getTenantBranding()} no Server Component do Next.js.
 */
public record TenantBrandingResponse(
    String tenantId,
    String slug,
    String nome,
    String uf,
    String tipoEntidade,
    String logoUrl,
    Cores cores,
    Set<String> modulosAtivos
) {

    public record Cores(String accent, String accentDark, String accentSecondary, String accentTertiary) {
    }

    public static TenantBrandingResponse de(Tenant tenant) {
        return new TenantBrandingResponse(
            tenant.getId().toString(),
            tenant.getSlug(),
            tenant.getNome(),
            tenant.getUf(),
            tenant.getTipoEntidade().name().toLowerCase(),
            tenant.getLogoUrl(),
            new Cores(
                tenant.getPaleta().accent(),
                tenant.getPaleta().accentDark(),
                tenant.getPaleta().accentSecondary(),
                tenant.getPaleta().accentTertiary()
            ),
            tenant.getModulosAtivos()
        );
    }
}
