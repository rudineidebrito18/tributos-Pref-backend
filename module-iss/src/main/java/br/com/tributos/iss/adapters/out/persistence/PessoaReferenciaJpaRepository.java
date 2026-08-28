package br.com.tributos.iss.adapters.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaReferenciaJpaRepository extends JpaRepository<PessoaReferenciaJpaEntity, UUID> {
}
