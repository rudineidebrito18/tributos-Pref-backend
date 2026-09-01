package br.com.tributos.iptu.adapters.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IptuBairroReferenciaJpaRepository extends JpaRepository<BairroReferenciaJpaEntity, UUID> {
}
