package br.com.tributos.cadastro.adapters.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LogradouroJpaRepository extends JpaRepository<LogradouroJpaEntity, UUID> {

    @Query("""
        SELECT l FROM LogradouroJpaEntity l
        WHERE l.cidadeId = :cidadeId
          AND (:bairroId IS NULL OR l.bairroId = :bairroId)
        ORDER BY l.nome
        """)
    List<LogradouroJpaEntity> listar(@Param("cidadeId") UUID cidadeId, @Param("bairroId") UUID bairroId);
}
