package br.com.tributos.identity.adapters.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantDominioJpaRepository extends JpaRepository<TenantDominioJpaEntity, UUID> {

    boolean existsByDominio(String dominio);
}
