package br.com.tributos.iss.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CertidaoIssJpaRepository extends JpaRepository<CertidaoIssJpaEntity, UUID> {

    @Query("SELECT COALESCE(MAX(c.numero), 0) FROM CertidaoIssJpaEntity c")
    long findMaxNumero();

    Optional<CertidaoIssJpaEntity> findByCodigoVerificacao(String codigoVerificacao);

    @Query("""
        SELECT c FROM CertidaoIssJpaEntity c
        WHERE (:contribuinteId IS NULL OR c.contribuinteId = :contribuinteId)
        ORDER BY c.numero DESC
        """)
    Page<CertidaoIssJpaEntity> buscarComFiltro(
        @Param("contribuinteId") UUID contribuinteId,
        Pageable pageable
    );
}
