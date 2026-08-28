package br.com.tributos.iss.adapters.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicoJpaRepository extends JpaRepository<ServicoJpaEntity, UUID> {

    boolean existsByCodigoLc116AndIdNot(String codigoLc116, UUID id);

    boolean existsByCodigoLc116(String codigoLc116);
}
