package br.com.tributos.iss.adapters.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ValorAlvaraJpaRepository extends JpaRepository<ValorAlvaraJpaEntity, UUID> {

    List<ValorAlvaraJpaEntity> findByTipoAlvaraIdOrderByAtualizadoEmDesc(UUID tipoAlvaraId);
}
