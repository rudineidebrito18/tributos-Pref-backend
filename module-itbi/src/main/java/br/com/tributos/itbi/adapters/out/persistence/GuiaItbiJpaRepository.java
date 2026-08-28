package br.com.tributos.itbi.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GuiaItbiJpaRepository extends JpaRepository<GuiaItbiJpaEntity, UUID> {

    @Query("""
        SELECT g FROM GuiaItbiJpaEntity g
        WHERE (:imovelId IS NULL OR g.imovelId = :imovelId)
        ORDER BY g.dataSolicitacao DESC
        """)
    Page<GuiaItbiJpaEntity> buscarComFiltro(@Param("imovelId") UUID imovelId, Pageable pageable);

    @Query("SELECT COALESCE(MAX(g.numero), 0) FROM GuiaItbiJpaEntity g")
    long maxNumero();
}
