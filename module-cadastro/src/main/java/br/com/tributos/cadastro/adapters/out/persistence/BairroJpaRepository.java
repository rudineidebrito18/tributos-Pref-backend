package br.com.tributos.cadastro.adapters.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BairroJpaRepository extends JpaRepository<BairroJpaEntity, UUID> {

    List<BairroJpaEntity> findByCidadeIdOrderByNome(UUID cidadeId);

    boolean existsByCidadeIdAndNome(UUID cidadeId, String nome);

    boolean existsByCidadeIdAndNomeAndIdNot(UUID cidadeId, String nome, UUID id);
}
