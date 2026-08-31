package br.com.tributos.itbi.adapters.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoCalculoGuiaItbiJpaRepository extends JpaRepository<TipoCalculoGuiaItbiJpaEntity, UUID> {

    boolean existsByDescricaoAndIdNot(String descricao, UUID id);

    boolean existsByDescricao(String descricao);
}
