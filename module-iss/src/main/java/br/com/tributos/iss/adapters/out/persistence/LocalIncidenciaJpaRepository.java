package br.com.tributos.iss.adapters.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalIncidenciaJpaRepository extends JpaRepository<LocalIncidenciaJpaEntity, UUID> {

    boolean existsByDescricao(String descricao);

    boolean existsByDescricaoAndIdNot(String descricao, UUID id);
}
