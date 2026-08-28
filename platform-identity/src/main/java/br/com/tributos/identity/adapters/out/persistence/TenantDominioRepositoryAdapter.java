package br.com.tributos.identity.adapters.out.persistence;

import org.springframework.stereotype.Component;

import br.com.tributos.identity.domain.TenantDominio;
import br.com.tributos.identity.domain.TenantDominioRepository;

@Component
public class TenantDominioRepositoryAdapter implements TenantDominioRepository {

    private final TenantDominioJpaRepository jpaRepository;

    public TenantDominioRepositoryAdapter(TenantDominioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void salvar(TenantDominio tenantDominio) {
        jpaRepository.save(new TenantDominioJpaEntity(
            tenantDominio.id(), tenantDominio.tenantId(), tenantDominio.dominio(),
            tenantDominio.verificado(), tenantDominio.criadoEm()
        ));
    }

    @Override
    public boolean existePorDominio(String dominio) {
        return jpaRepository.existsByDominio(dominio);
    }
}
