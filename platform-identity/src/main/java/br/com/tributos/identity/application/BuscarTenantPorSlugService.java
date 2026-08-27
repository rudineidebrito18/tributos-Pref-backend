package br.com.tributos.identity.application;

import org.springframework.stereotype.Service;

import br.com.tributos.identity.domain.Tenant;
import br.com.tributos.identity.domain.TenantRepository;
import br.com.tributos.kernel.exception.NotFoundException;

/**
 * Caso de uso: resolver um tenant pelo slug do subdomínio. Usado hoje só pelo endpoint
 * público de branding (adapters.in.web.TenantPublicController); é o mesmo ponto que, a
 * partir do Sprint 1, o {@code TenantResolverFilter} vai chamar para validar o tenant de
 * toda requisição autenticada.
 *
 * <p>{@code @Service} é a única concessão a Spring nesta camada de aplicação — deliberada:
 * evita um arquivo de configuração manual por caso de uso só para expor um {@code @Bean},
 * sem comprometer a regra real (zero Spring/JPA/HTTP dentro de {@code domain}, que é onde
 * mora a lógica de negócio de fato).
 */
@Service
public class BuscarTenantPorSlugService {

    private final TenantRepository tenantRepository;

    public BuscarTenantPorSlugService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public Tenant executar(String slug) {
        Tenant tenant = tenantRepository.buscarPorSlug(slug)
            .orElseThrow(() -> NotFoundException.de("Tenant", slug));

        if (!tenant.isAtivo()) {
            throw NotFoundException.de("Tenant", slug);
        }

        return tenant;
    }
}
