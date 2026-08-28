package br.com.tributos.iss.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusCredenciamentoJpaRepository extends JpaRepository<StatusCredenciamentoJpaEntity, UUID> {

    boolean existsByNomeAndIdNot(String nome, UUID id);

    boolean existsByNome(String nome);

    Optional<StatusCredenciamentoJpaEntity> findByNome(String nome);
}
