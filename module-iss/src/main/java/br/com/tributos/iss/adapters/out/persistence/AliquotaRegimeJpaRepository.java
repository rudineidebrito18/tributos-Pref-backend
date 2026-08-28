package br.com.tributos.iss.adapters.out.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AliquotaRegimeJpaRepository extends JpaRepository<AliquotaRegimeJpaEntity, UUID> {

    List<AliquotaRegimeJpaEntity> findByRegimeIdOrderByFaixaReceitaMinAsc(UUID regimeId);

    @Query("""
        SELECT a FROM AliquotaRegimeJpaEntity a
        WHERE a.regimeId = :regimeId
        AND a.competenciaVigencia = (
            SELECT MAX(a2.competenciaVigencia) FROM AliquotaRegimeJpaEntity a2
            WHERE a2.regimeId = :regimeId AND a2.competenciaVigencia <= :competencia
        )
        ORDER BY a.faixaReceitaMin ASC
        """)
    List<AliquotaRegimeJpaEntity> findVigentesPorRegime(
        @Param("regimeId") UUID regimeId,
        @Param("competencia") LocalDate competencia
    );
}
