package br.com.tributos.iss.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SolicitacaoCredenciamentoJpaRepository extends JpaRepository<SolicitacaoCredenciamentoJpaEntity, UUID> {

    Page<SolicitacaoCredenciamentoJpaEntity> findAllByOrderByCriadoEmDesc(Pageable pageable);

    @Query("""
        SELECT s FROM SolicitacaoCredenciamentoJpaEntity s
        WHERE s.contribuinteId = :contribuinteId
          AND s.statusId = :statusEmAnaliseId
        ORDER BY s.criadoEm DESC
        """)
    Optional<SolicitacaoCredenciamentoJpaEntity> findEmAnalisePorContribuinte(
        @Param("contribuinteId") UUID contribuinteId,
        @Param("statusEmAnaliseId") UUID statusEmAnaliseId
    );
}
