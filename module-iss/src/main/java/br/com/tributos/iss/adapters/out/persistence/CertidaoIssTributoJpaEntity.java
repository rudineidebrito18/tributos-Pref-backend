package br.com.tributos.iss.adapters.out.persistence;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import br.com.tributos.iss.domain.TributoCertidao;

@Entity
@Table(name = "iss_certidao_tributo")
public class CertidaoIssTributoJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "certidao_id", nullable = false)
    private UUID certidaoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TributoCertidao tributo;

    protected CertidaoIssTributoJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public UUID getCertidaoId() {
        return certidaoId;
    }

    public void setCertidaoId(UUID certidaoId) {
        this.certidaoId = certidaoId;
    }

    public TributoCertidao getTributo() {
        return tributo;
    }

    public void setTributo(TributoCertidao tributo) {
        this.tributo = tributo;
    }
}
