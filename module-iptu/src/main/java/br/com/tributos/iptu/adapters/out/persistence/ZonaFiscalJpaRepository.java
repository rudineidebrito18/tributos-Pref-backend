package br.com.tributos.iptu.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ZonaFiscalJpaRepository extends JpaRepository<ZonaFiscalJpaEntity, UUID> {

    List<ZonaFiscalJpaEntity> findAllByOrderByNome();

    List<ZonaFiscalJpaEntity> findByAtivoTrueOrderByNome();

    boolean existsByNome(String nome);

    boolean existsByNomeAndIdNot(String nome, UUID id);
}
