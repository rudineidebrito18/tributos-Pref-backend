package br.com.tributos.iss.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlvaraJpaRepository extends JpaRepository<AlvaraJpaEntity, UUID> {

    @Query("SELECT COALESCE(MAX(a.numero), 0) FROM AlvaraJpaEntity a")
    long findMaxNumero();

    Optional<AlvaraJpaEntity> findByCodigoVerificacao(String codigoVerificacao);

    @Query("""
        SELECT a FROM AlvaraJpaEntity a
        WHERE (:contribuinteId IS NULL OR a.contribuinteId = :contribuinteId)
        ORDER BY a.numero DESC
        """)
    Page<AlvaraJpaEntity> buscarComFiltro(
        @Param("contribuinteId") UUID contribuinteId,
        Pageable pageable
    );
}
