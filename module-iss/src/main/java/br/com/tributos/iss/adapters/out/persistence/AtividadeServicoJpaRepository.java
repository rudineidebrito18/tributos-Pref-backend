package br.com.tributos.iss.adapters.out.persistence;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AtividadeServicoJpaRepository extends JpaRepository<AtividadeServicoJpaEntity, UUID> {

    boolean existsByAtividadeIdAndServicoIdAndIdNot(UUID atividadeId, UUID servicoId, UUID id);

    boolean existsByAtividadeIdAndServicoId(UUID atividadeId, UUID servicoId);

    java.util.Optional<AtividadeServicoJpaEntity> findByAtividadeIdAndServicoId(UUID atividadeId, UUID servicoId);

    @Query("""
        SELECT a FROM AtividadeServicoJpaEntity a
        JOIN AtividadeJpaEntity at ON at.id = a.atividadeId
        LEFT JOIN ServicoJpaEntity s ON s.id = a.servicoId
        WHERE (:codigoCnae IS NULL OR at.codigo LIKE :codigoCnae)
          AND (:codigoServico IS NULL OR s.codigoLc116 LIKE :codigoServico)
        """)
    Page<AtividadeServicoJpaEntity> listarComFiltro(
        @Param("codigoCnae") String codigoCnae,
        @Param("codigoServico") String codigoServico,
        Pageable pageable
    );

    @Query("""
        SELECT a.id AS id,
               at.codigo AS cnae,
               s.codigoLc116 AS codigo,
               s.descricao AS servico,
               a.aliquota AS aliquota,
               a.tributavel AS tributavel,
               a.deducao AS deducao,
               a.retencaoFonte AS retencao,
               l.descricao AS incidencia
        FROM AtividadeServicoJpaEntity a
        JOIN AtividadeJpaEntity at ON at.id = a.atividadeId
        LEFT JOIN ServicoJpaEntity s ON s.id = a.servicoId
        JOIN LocalIncidenciaJpaEntity l ON l.id = a.localIncidenciaId
        WHERE (:codigoCnae IS NULL OR at.codigo LIKE :codigoCnae)
          AND (:codigoServico IS NULL OR s.codigoLc116 LIKE :codigoServico)
        """)
    Page<AtividadeServicoViewProjection> listarView(
        @Param("codigoCnae") String codigoCnae,
        @Param("codigoServico") String codigoServico,
        Pageable pageable
    );
}
