package br.com.tributos.iss.adapters.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AtividadeJpaRepository extends JpaRepository<AtividadeJpaEntity, UUID> {

    boolean existsByCodigoAndIdNot(String codigo, UUID id);

    boolean existsByCodigo(String codigo);
}
