package br.com.tributos.iss.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AtividadeJpaRepository extends JpaRepository<AtividadeJpaEntity, UUID> {

    boolean existsByCodigoAndIdNot(String codigo, UUID id);

    boolean existsByCodigo(String codigo);

    @Query("SELECT a FROM AtividadeJpaEntity a WHERE (:isServico IS NULL OR a.isServico = :isServico)")
    List<AtividadeJpaEntity> listarComFiltro(@Param("isServico") Boolean isServico);
}
