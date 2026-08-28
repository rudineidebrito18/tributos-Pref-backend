package br.com.tributos.iptu.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.tributos.iptu.domain.SituacaoImovel;

public interface ImovelJpaRepository extends JpaRepository<ImovelJpaEntity, UUID> {

    @Query("SELECT COALESCE(MAX(i.numeroCadastro), 0) FROM ImovelJpaEntity i")
    long findMaxNumeroCadastro();

    Optional<ImovelJpaEntity> findByCodigoLegado(String codigoLegado);

    @Query("""
        SELECT i FROM ImovelJpaEntity i
        WHERE (:busca IS NULL OR :busca = ''
            OR CAST(i.numeroCadastro AS string) LIKE CONCAT('%', :busca, '%')
            OR LOWER(i.codigoLegado) LIKE LOWER(CONCAT('%', :busca, '%')))
        ORDER BY i.numeroCadastro
        """)
    Page<ImovelJpaEntity> buscarComFiltro(@Param("busca") String busca, Pageable pageable);

    List<ImovelJpaEntity> findBySituacaoAndZonaFiscalIdIsNotNullAndDestinacaoIdIsNotNull(SituacaoImovel situacao);

    long countBySituacaoAndZonaFiscalIdIsNull(SituacaoImovel situacao);
}
