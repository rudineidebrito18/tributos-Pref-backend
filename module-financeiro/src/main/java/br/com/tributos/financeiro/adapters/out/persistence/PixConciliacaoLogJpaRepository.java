package br.com.tributos.financeiro.adapters.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface PixConciliacaoLogJpaRepository extends JpaRepository<PixConciliacaoLogJpaEntity, UUID> {

    List<PixConciliacaoLogJpaEntity> findByGuiaIdOrderByCriadoEmDesc(UUID guiaId);
}
