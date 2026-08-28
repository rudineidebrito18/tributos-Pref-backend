package br.com.tributos.iptu.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AliquotaIptuJpaRepository extends JpaRepository<AliquotaIptuJpaEntity, UUID> {

    Optional<AliquotaIptuJpaEntity> findByExercicioAndDestinacaoIdAndZonaFiscalId(
        int exercicio,
        UUID destinacaoId,
        UUID zonaFiscalId
    );

    List<AliquotaIptuJpaEntity> findByExercicioOrderByDestinacaoIdAscZonaFiscalIdAsc(int exercicio);
}
