package br.com.tributos.iptu.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ValorTerrenoM2JpaRepository extends JpaRepository<ValorTerrenoM2JpaEntity, UUID> {

    Optional<ValorTerrenoM2JpaEntity> findByZonaFiscalIdAndExercicio(UUID zonaFiscalId, int exercicio);

    List<ValorTerrenoM2JpaEntity> findByExercicioOrderByZonaFiscalId(int exercicio);
}
