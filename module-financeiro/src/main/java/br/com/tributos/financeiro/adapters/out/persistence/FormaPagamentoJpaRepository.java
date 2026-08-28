package br.com.tributos.financeiro.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FormaPagamentoJpaRepository extends JpaRepository<FormaPagamentoJpaEntity, UUID> {

    Optional<FormaPagamentoJpaEntity> findByCodigo(String codigo);
}
