package br.com.tributos.iss.adapters.out.persistence;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotaFiscalJpaRepository extends JpaRepository<NotaFiscalJpaEntity, UUID> {

    @Query("SELECT COALESCE(MAX(n.numero), 0) FROM NotaFiscalJpaEntity n")
    long findMaxNumero();

    @Query("""
        SELECT n FROM NotaFiscalJpaEntity n
        WHERE (:contribuinteId IS NULL OR n.contribuinteId = :contribuinteId)
          AND (:tomadorId IS NULL OR n.tomadorId = :tomadorId)
          AND (:competencia IS NULL OR n.competencia = :competencia)
        ORDER BY n.numero DESC
        """)
    Page<NotaFiscalJpaEntity> buscarComFiltro(
        @Param("contribuinteId") UUID contribuinteId,
        @Param("tomadorId") UUID tomadorId,
        @Param("competencia") LocalDate competencia,
        Pageable pageable
    );
}
