package br.com.tributos.iptu.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IptuContribuinteReferenciaJpaRepository extends JpaRepository<ContribuinteReferenciaJpaEntity, UUID> {

    Optional<ContribuinteReferenciaJpaEntity> findFirstByPessoaId(UUID pessoaId);
}
