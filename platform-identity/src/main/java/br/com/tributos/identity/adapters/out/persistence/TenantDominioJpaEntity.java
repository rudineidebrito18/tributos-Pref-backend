package br.com.tributos.identity.adapters.out.persistence;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidade JPA — mapeia a tabela {@code tenant_dominio} (ver
 * db/migration/platform/V5__tenant_dominio_e_plataforma_admin.sql). Sem RLS, assim como
 * {@code tenant}: precisa ser consultável pela resolução de tenant por domínio próprio,
 * antes de qualquer autenticação.
 */
@Entity
@Table(name = "tenant_dominio")
public class TenantDominioJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false, unique = true)
    private String dominio;

    @Column(nullable = false)
    private boolean verificado;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    protected TenantDominioJpaEntity() {
        // exigido pelo JPA
    }

    public TenantDominioJpaEntity(UUID id, UUID tenantId, String dominio, boolean verificado, Instant criadoEm) {
        this.id = id;
        this.tenantId = tenantId;
        this.dominio = dominio;
        this.verificado = verificado;
        this.criadoEm = criadoEm;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public String getDominio() {
        return dominio;
    }

    public boolean isVerificado() {
        return verificado;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
