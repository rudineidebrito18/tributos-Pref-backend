package br.com.tributos.iptu.adapters.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ImovelTipoEdificacaoJpaRepository extends JpaRepository<ImovelTipoEdificacaoJpaEntity, UUID> {

    boolean existsByNomeAndIdNot(String nome, UUID id);

    boolean existsByNome(String nome);
}
