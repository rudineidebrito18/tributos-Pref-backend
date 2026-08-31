package br.com.tributos.cadastro.adapters.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentoCategoriaJpaRepository extends JpaRepository<DocumentoCategoriaJpaEntity, UUID> {

    List<DocumentoCategoriaJpaEntity> findByTenantIdOrderByNomeAsc(UUID tenantId);

    @Query("""
        SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
        FROM DocumentoCategoriaJpaEntity c
        WHERE c.tenantId = :tenantId
          AND LOWER(c.nome) = LOWER(:nome)
          AND (:excluirId IS NULL OR c.id <> :excluirId)
        """)
    boolean existsPorNome(
        @Param("tenantId") UUID tenantId,
        @Param("nome") String nome,
        @Param("excluirId") UUID excluirId
    );
}
