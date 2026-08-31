package br.com.tributos.identity.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.tributos.identity.domain.AmbientePixBb;

public interface ConfiguracaoPixBbJpaRepository extends JpaRepository<ConfiguracaoPixBbJpaEntity, UUID> {

    List<ConfiguracaoPixBbJpaEntity> findByTenantIdOrderByAmbiente(UUID tenantId);

    Optional<ConfiguracaoPixBbJpaEntity> findByTenantIdAndAmbiente(UUID tenantId, AmbientePixBb ambiente);

    Optional<ConfiguracaoPixBbJpaEntity> findByTenantIdAndAtivoTrue(UUID tenantId);

    @Modifying
    @Query("""
        UPDATE ConfiguracaoPixBbJpaEntity c
        SET c.ativo = false
        WHERE c.tenantId = :tenantId AND c.id <> :excetoId
        """)
    void desativarOutras(@Param("tenantId") UUID tenantId, @Param("excetoId") UUID excetoId);
}
