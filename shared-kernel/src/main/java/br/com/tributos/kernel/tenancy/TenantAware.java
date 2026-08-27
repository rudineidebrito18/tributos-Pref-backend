package br.com.tributos.kernel.tenancy;

import java.util.UUID;

/**
 * Interface marcadora implementada por toda entidade JPA multi-tenant. Um
 * {@code EntityListener} genérico (em app-bootstrap) usa esta interface para preencher
 * {@code tenantId} automaticamente em {@code @PrePersist}, a partir de
 * {@link TenantContext#getObrigatorio()} — assim nenhum código de módulo de domínio
 * precisa (ou consegue, por engano) setar o tenant de uma entidade manualmente.
 */
public interface TenantAware {

    UUID getTenantId();

    void setTenantId(UUID tenantId);
}
