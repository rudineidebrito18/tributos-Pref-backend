package br.com.tributos.iptu.adapters.out.persistence;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HabiteseImovelJpaRepository extends JpaRepository<HabiteseImovelJpaEntity, UUID> {

    @Query("SELECT COALESCE(MAX(h.numero), 0) FROM HabiteseImovelJpaEntity h")
    long findMaxNumero();

    Page<HabiteseImovelJpaEntity> findByImovelIdOrderByNumeroDesc(UUID imovelId, Pageable pageable);

    boolean existsByCodigoVerificacao(String codigoVerificacao);
}
