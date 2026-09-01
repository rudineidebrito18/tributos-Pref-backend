package br.com.tributos.itbi.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItbiContribuinteReferenciaJpaRepository extends JpaRepository<ContribuinteReferenciaJpaEntity, UUID> {

    Optional<ContribuinteReferenciaJpaEntity> findFirstByPessoaId(UUID pessoaId);
}
