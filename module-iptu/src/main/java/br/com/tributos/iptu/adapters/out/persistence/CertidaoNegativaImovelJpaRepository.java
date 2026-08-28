package br.com.tributos.iptu.adapters.out.persistence;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CertidaoNegativaImovelJpaRepository extends JpaRepository<CertidaoNegativaImovelJpaEntity, UUID> {

    @Query("SELECT COALESCE(MAX(c.numero), 0) FROM CertidaoNegativaImovelJpaEntity c")
    long findMaxNumero();

    Page<CertidaoNegativaImovelJpaEntity> findByImovelIdOrderByNumeroDesc(UUID imovelId, Pageable pageable);
}
