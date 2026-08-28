package br.com.tributos.financeiro.adapters.out.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.tributos.financeiro.domain.OrigemGuia;
import br.com.tributos.financeiro.domain.SituacaoGuia;
import br.com.tributos.financeiro.domain.TipoTributo;

public interface GuiaArrecadacaoJpaRepository extends JpaRepository<GuiaArrecadacaoJpaEntity, UUID> {

    Optional<GuiaArrecadacaoJpaEntity> findByOrigemTipoAndOrigemId(OrigemGuia origemTipo, UUID origemId);

    Optional<GuiaArrecadacaoJpaEntity> findByNumero(long numero);

    @Query("""
        SELECT g FROM GuiaArrecadacaoJpaEntity g
        WHERE (:tipoTributo IS NULL OR g.tipoTributo = :tipoTributo)
          AND (:situacao IS NULL OR g.situacao = :situacao)
          AND (:contribuinteId IS NULL OR g.contribuinteId = :contribuinteId)
        ORDER BY g.dataEmissao DESC
        """)
    Page<GuiaArrecadacaoJpaEntity> buscarComFiltro(
        @Param("tipoTributo") TipoTributo tipoTributo,
        @Param("situacao") SituacaoGuia situacao,
        @Param("contribuinteId") UUID contribuinteId,
        Pageable pageable
    );

    @Query("SELECT COALESCE(MAX(g.numero), 0) FROM GuiaArrecadacaoJpaEntity g")
    long maxNumero();

    boolean existsByTenantIdAndContribuinteIdAndSituacao(UUID tenantId, UUID contribuinteId, SituacaoGuia situacao);

    @Query("""
        SELECT g FROM GuiaArrecadacaoJpaEntity g
        WHERE g.situacao = br.com.tributos.financeiro.domain.SituacaoGuia.PAGA
          AND g.dataEfetivacao >= :inicio AND g.dataEfetivacao < :fim
          AND (:tipoTributo IS NULL OR g.tipoTributo = :tipoTributo)
        """)
    List<GuiaArrecadacaoJpaEntity> buscarPagasNoPeriodo(
        @Param("inicio") Instant inicio,
        @Param("fim") Instant fim,
        @Param("tipoTributo") TipoTributo tipoTributo
    );
}
