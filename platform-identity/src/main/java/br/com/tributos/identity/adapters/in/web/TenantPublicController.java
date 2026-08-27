package br.com.tributos.identity.adapters.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.tributos.identity.adapters.in.web.dto.TenantBrandingResponse;
import br.com.tributos.identity.application.BuscarTenantPorSlugService;

/**
 * Endpoint PÚBLICO (sem autenticação — precisa responder antes do login, para pintar logo
 * e cores da tela de login). Consumido por {@code getTenantBranding()} no frontend
 * (Server Component de layout.tsx), que já trata 404 caindo num branding padrão — ver
 * PLANEJAMENTO_PROJETO.md §8.1.
 *
 * <p>Por estar sob {@code /api/public/**}, o {@code SecurityConfig} (app-bootstrap) deve
 * liberar este caminho explicitamente do filtro de autenticação JWT.
 */
@RestController
@RequestMapping("/api/public/tenants")
public class TenantPublicController {

    private final BuscarTenantPorSlugService buscarTenantPorSlugService;

    public TenantPublicController(BuscarTenantPorSlugService buscarTenantPorSlugService) {
        this.buscarTenantPorSlugService = buscarTenantPorSlugService;
    }

    @GetMapping("/{slug}/branding")
    public TenantBrandingResponse buscarBranding(@PathVariable String slug) {
        return TenantBrandingResponse.de(buscarTenantPorSlugService.executar(slug));
    }
}
