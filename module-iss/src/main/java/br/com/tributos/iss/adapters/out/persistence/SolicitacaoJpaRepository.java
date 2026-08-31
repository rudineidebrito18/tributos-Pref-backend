package br.com.tributos.iss.adapters.out.persistence;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SolicitacaoJpaRepository extends JpaRepository<SolicitacaoJpaEntity, UUID> {

    @Query("""
        SELECT s FROM SolicitacaoJpaEntity s
        WHERE (:tipoId IS NULL OR s.tipoSolicitacaoId = :tipoId)
          AND (:statusId IS NULL OR s.statusSolicitacaoId = :statusId)
        ORDER BY s.dataHora DESC
        """)
    Page<SolicitacaoJpaEntity> buscarComFiltro(
        @Param("tipoId") UUID tipoId,
        @Param("statusId") UUID statusId,
        Pageable pageable
    );
}
