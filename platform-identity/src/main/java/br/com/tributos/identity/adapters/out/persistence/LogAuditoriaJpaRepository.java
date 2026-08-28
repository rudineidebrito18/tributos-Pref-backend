package br.com.tributos.identity.adapters.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LogAuditoriaJpaRepository extends JpaRepository<LogAuditoriaJpaEntity, UUID> {
}
