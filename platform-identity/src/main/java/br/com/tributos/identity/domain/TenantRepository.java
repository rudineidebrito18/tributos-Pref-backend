package br.com.tributos.identity.domain;

import java.util.Optional;

/**
 * Porta de saída (Hexagonal Architecture) para persistência de {@link Tenant}. A
 * implementação real (JPA) mora em
 * {@code adapters.out.persistence.TenantRepositoryAdapter} — este módulo de domínio nunca
 * importa Spring Data diretamente.
 */
public interface TenantRepository {

    Optional<Tenant> buscarPorSlug(String slug);
}
