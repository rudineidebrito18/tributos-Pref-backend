package br.com.tributos.iptu.adapters.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LancamentoParcelaJpaRepository extends JpaRepository<LancamentoParcelaJpaEntity, UUID> {

    List<LancamentoParcelaJpaEntity> findByLancamentoIdOrderByNumeroParcelaAsc(UUID lancamentoId);
}
