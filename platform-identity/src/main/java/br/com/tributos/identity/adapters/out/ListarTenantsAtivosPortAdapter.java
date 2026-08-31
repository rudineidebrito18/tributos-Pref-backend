package br.com.tributos.identity.adapters.out;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.identity.adapters.out.persistence.TenantJpaRepository;
import br.com.tributos.kernel.tenancy.ListarTenantsAtivosPort;

@Component
public class ListarTenantsAtivosPortAdapter implements ListarTenantsAtivosPort {

    private final TenantJpaRepository tenantJpaRepository;

    public ListarTenantsAtivosPortAdapter(TenantJpaRepository tenantJpaRepository) {
        this.tenantJpaRepository = tenantJpaRepository;
    }

    @Override
    public List<UUID> listarIds() {
        return tenantJpaRepository.findByAtivoTrue().stream()
            .map(e -> e.getId())
            .toList();
    }
}
