package br.com.tributos.cadastro.adapters.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentoJpaRepository extends JpaRepository<DocumentoJpaEntity, UUID> {

    List<DocumentoJpaEntity> findByPessoaIdOrderByCriadoEmDesc(UUID pessoaId);

    @Query("""
        SELECT d FROM DocumentoJpaEntity d
        WHERE d.tenantId = :tenantId
          AND d.pessoaId IS NULL
          AND (:titulo IS NULL OR :titulo = '' OR LOWER(d.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')))
          AND (:categoriaId IS NULL OR d.categoriaId = :categoriaId)
          AND (:nomeArquivo IS NULL OR :nomeArquivo = '' OR LOWER(d.nomeArquivo) LIKE LOWER(CONCAT('%', :nomeArquivo, '%')))
        ORDER BY d.criadoEm DESC
        """)
    Page<DocumentoJpaEntity> listarSistema(
        @Param("tenantId") UUID tenantId,
        @Param("titulo") String titulo,
        @Param("categoriaId") UUID categoriaId,
        @Param("nomeArquivo") String nomeArquivo,
        Pageable pageable
    );

    @Query("""
        SELECT d FROM DocumentoJpaEntity d
        JOIN DocumentoCompartilhamentoJpaEntity c ON c.documentoId = d.id
        WHERE d.tenantId = :tenantId
          AND c.usuarioId = :usuarioId
          AND d.pessoaId IS NULL
          AND (:titulo IS NULL OR :titulo = '' OR LOWER(d.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')))
          AND (:categoriaId IS NULL OR d.categoriaId = :categoriaId)
          AND (:nomeArquivo IS NULL OR :nomeArquivo = '' OR LOWER(d.nomeArquivo) LIKE LOWER(CONCAT('%', :nomeArquivo, '%')))
        ORDER BY d.criadoEm DESC
        """)
    Page<DocumentoJpaEntity> listarCompartilhadosComUsuario(
        @Param("tenantId") UUID tenantId,
        @Param("usuarioId") UUID usuarioId,
        @Param("titulo") String titulo,
        @Param("categoriaId") UUID categoriaId,
        @Param("nomeArquivo") String nomeArquivo,
        Pageable pageable
    );
}
