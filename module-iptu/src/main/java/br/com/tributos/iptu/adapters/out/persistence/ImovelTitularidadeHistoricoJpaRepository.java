package br.com.tributos.iptu.adapters.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ImovelTitularidadeHistoricoJpaRepository extends JpaRepository<ImovelTitularidadeHistoricoJpaEntity, UUID> {

    List<ImovelTitularidadeHistoricoJpaEntity> findByImovelIdOrderByDataRegistroDesc(UUID imovelId);
}
