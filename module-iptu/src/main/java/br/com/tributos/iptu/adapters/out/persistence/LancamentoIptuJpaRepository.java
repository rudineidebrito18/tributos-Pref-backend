package br.com.tributos.iptu.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LancamentoIptuJpaRepository extends JpaRepository<LancamentoIptuJpaEntity, UUID> {

    Optional<LancamentoIptuJpaEntity> findByImovelIdAndExercicio(UUID imovelId, int exercicio);

    @Query("""
        SELECT l FROM LancamentoIptuJpaEntity l
        WHERE (:exercicio IS NULL OR l.exercicio = :exercicio)
          AND (:imovelId IS NULL OR l.imovelId = :imovelId)
        ORDER BY l.exercicio DESC, l.dataGeracao DESC
        """)
    Page<LancamentoIptuJpaEntity> buscarComFiltro(
        @Param("exercicio") Integer exercicio,
        @Param("imovelId") UUID imovelId,
        Pageable pageable
    );
}
