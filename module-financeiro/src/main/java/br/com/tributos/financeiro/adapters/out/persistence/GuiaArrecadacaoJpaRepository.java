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
import br.com.tributos.financeiro.domain.StatusPix;
import br.com.tributos.financeiro.domain.TipoTributo;

public interface GuiaArrecadacaoJpaRepository extends JpaRepository<GuiaArrecadacaoJpaEntity, UUID> {

    Optional<GuiaArrecadacaoJpaEntity> findByOrigemTipoAndOrigemId(OrigemGuia origemTipo, UUID origemId);

    Optional<GuiaArrecadacaoJpaEntity> findByNumero(long numero);

    Optional<GuiaArrecadacaoJpaEntity> findByTenantIdAndPixTxid(UUID tenantId, String pixTxid);

    @Query("""
        SELECT g FROM GuiaArrecadacaoJpaEntity g
        WHERE (:tipoTributo IS NULL OR g.tipoTributo = :tipoTributo)
          AND (:situacao IS NULL OR g.situacao = :situacao)
          AND (:contribuinteId IS NULL OR g.contribuinteId = :contribuinteId)
          AND (:statusPix IS NULL OR g.statusPix = :statusPix)
          AND (:formaPagamentoId IS NULL OR g.formaPagamentoId = :formaPagamentoId)
          AND (:origemTipo IS NULL OR g.origemTipo = :origemTipo)
        ORDER BY g.dataEmissao DESC
        """)
    Page<GuiaArrecadacaoJpaEntity> buscarComFiltro(
        @Param("tipoTributo") TipoTributo tipoTributo,
        @Param("situacao") SituacaoGuia situacao,
        @Param("contribuinteId") UUID contribuinteId,
        @Param("statusPix") StatusPix statusPix,
        @Param("formaPagamentoId") UUID formaPagamentoId,
        @Param("origemTipo") OrigemGuia origemTipo,
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

    @Query("""
        SELECT g FROM GuiaArrecadacaoJpaEntity g
        WHERE g.statusPix = br.com.tributos.financeiro.domain.StatusPix.ATIVA
          AND g.pixTxid IS NOT NULL
          AND g.pixSolicitadoEm >= :desde
        ORDER BY g.pixSolicitadoEm ASC
        """)
    List<GuiaArrecadacaoJpaEntity> buscarAtivasParaConciliacao(
        @Param("desde") Instant desde,
        Pageable pageable
    );
}
