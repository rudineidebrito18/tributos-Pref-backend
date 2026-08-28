package br.com.tributos.iss.adapters.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SituacaoCadastralJpaRepository extends JpaRepository<SituacaoCadastralJpaEntity, UUID> {

    boolean existsByNomeAndIdNot(String nome, UUID id);

    boolean existsByNome(String nome);
}
