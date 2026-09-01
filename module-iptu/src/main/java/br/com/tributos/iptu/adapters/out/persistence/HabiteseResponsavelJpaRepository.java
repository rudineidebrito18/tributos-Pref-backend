package br.com.tributos.iptu.adapters.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HabiteseResponsavelJpaRepository extends JpaRepository<HabiteseResponsavelJpaEntity, UUID> {

    void deleteByHabiteseId(UUID habiteseId);

    java.util.List<HabiteseResponsavelJpaEntity> findByHabiteseIdOrderByOrdemAsc(UUID habiteseId);
}
