package br.com.tributos.iss.adapters.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusSolicitacaoJpaRepository extends JpaRepository<StatusSolicitacaoJpaEntity, UUID> {
}
