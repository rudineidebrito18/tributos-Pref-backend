package br.com.tributos.identity.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PapelJpaRepository extends JpaRepository<PapelJpaEntity, UUID> {

    Optional<PapelJpaEntity> findByNome(String nome);
}
