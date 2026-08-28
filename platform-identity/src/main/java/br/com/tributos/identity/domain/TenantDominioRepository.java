package br.com.tributos.identity.domain;

/**
 * Porta de saída para persistência de {@link TenantDominio}. Implementação real em
 * {@code adapters.out.persistence.TenantDominioRepositoryAdapter}.
 */
public interface TenantDominioRepository {

    void salvar(TenantDominio tenantDominio);

    boolean existePorDominio(String dominio);
}
