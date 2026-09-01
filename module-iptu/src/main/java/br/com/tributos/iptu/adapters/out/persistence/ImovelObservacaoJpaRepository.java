package br.com.tributos.iptu.adapters.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ImovelObservacaoJpaRepository extends JpaRepository<ImovelObservacaoJpaEntity, UUID> {

    List<ImovelObservacaoJpaEntity> findByImovelIdOrderByCriadoEmDesc(UUID imovelId);
}
