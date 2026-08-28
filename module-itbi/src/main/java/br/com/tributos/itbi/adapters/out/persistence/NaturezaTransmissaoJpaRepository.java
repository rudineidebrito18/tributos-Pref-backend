package br.com.tributos.itbi.adapters.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NaturezaTransmissaoJpaRepository extends JpaRepository<NaturezaTransmissaoJpaEntity, UUID> {

    List<NaturezaTransmissaoJpaEntity> findByAtivoTrueOrderByNome();
}
