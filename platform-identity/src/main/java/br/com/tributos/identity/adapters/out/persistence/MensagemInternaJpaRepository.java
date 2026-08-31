package br.com.tributos.identity.adapters.out.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MensagemInternaJpaRepository extends JpaRepository<MensagemInternaJpaEntity, UUID> {

    @Query("""
        SELECT DISTINCT m FROM MensagemInternaJpaEntity m
        JOIN m.destinatarios d
        WHERE d.destinatarioId = :usuarioId
          AND d.arquivadaEm IS NULL
          AND (:assuntoPattern IS NULL OR LOWER(m.assunto) LIKE :assuntoPattern)
          AND (:corpoPattern IS NULL OR LOWER(m.corpo) LIKE :corpoPattern)
        ORDER BY m.criadoEm DESC
        """)
    Page<MensagemInternaJpaEntity> listarEntrada(
        @Param("usuarioId") UUID usuarioId,
        @Param("assuntoPattern") String assuntoPattern,
        @Param("corpoPattern") String corpoPattern,
        Pageable pageable
    );

    @Query("""
        SELECT m FROM MensagemInternaJpaEntity m
        WHERE m.remetenteId = :usuarioId
          AND (:assuntoPattern IS NULL OR LOWER(m.assunto) LIKE :assuntoPattern)
          AND (:corpoPattern IS NULL OR LOWER(m.corpo) LIKE :corpoPattern)
        ORDER BY m.criadoEm DESC
        """)
    Page<MensagemInternaJpaEntity> listarEnviadas(
        @Param("usuarioId") UUID usuarioId,
        @Param("assuntoPattern") String assuntoPattern,
        @Param("corpoPattern") String corpoPattern,
        Pageable pageable
    );

    @Query("""
        SELECT m FROM MensagemInternaJpaEntity m
        JOIN m.destinatarios d
        WHERE d.destinatarioId = :usuarioId
          AND d.arquivadaEm IS NOT NULL
          AND (:assuntoPattern IS NULL OR LOWER(m.assunto) LIKE :assuntoPattern)
          AND (:corpoPattern IS NULL OR LOWER(m.corpo) LIKE :corpoPattern)
        ORDER BY m.criadoEm DESC
        """)
    Page<MensagemInternaJpaEntity> listarArquivadas(
        @Param("usuarioId") UUID usuarioId,
        @Param("assuntoPattern") String assuntoPattern,
        @Param("corpoPattern") String corpoPattern,
        Pageable pageable
    );

    @Query("""
        SELECT m FROM MensagemInternaJpaEntity m
        LEFT JOIN FETCH m.destinatarios
        WHERE m.id = :id
        """)
    Optional<MensagemInternaJpaEntity> buscarComDestinatarios(@Param("id") UUID id);

    @Query("""
        SELECT DISTINCT m FROM MensagemInternaJpaEntity m
        LEFT JOIN FETCH m.destinatarios
        WHERE m.id IN :ids
        """)
    List<MensagemInternaJpaEntity> buscarComDestinatariosPorIds(@Param("ids") Collection<UUID> ids);
}
