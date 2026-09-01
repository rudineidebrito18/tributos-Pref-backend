package br.com.tributos.iptu.adapters.out.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "imovel_proprietario")
public class ImovelProprietarioJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "imovel_id", nullable = false)
    private UUID imovelId;

    @Column(name = "contribuinte_id", nullable = false)
    private UUID contribuinteId;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal porcentagem;

    @Column(name = "proprietario_principal", nullable = false)
    private boolean proprietarioPrincipal;

    @Column(name = "criado_em", nullable = false, updatable = false, insertable = false)
    private Instant criadoEm;

    protected ImovelProprietarioJpaEntity() {
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

    public UUID getImovelId() {
        return imovelId;
    }

    public void setImovelId(UUID imovelId) {
        this.imovelId = imovelId;
    }

    public UUID getContribuinteId() {
        return contribuinteId;
    }

    public void setContribuinteId(UUID contribuinteId) {
        this.contribuinteId = contribuinteId;
    }

    public BigDecimal getPorcentagem() {
        return porcentagem;
    }

    public void setPorcentagem(BigDecimal porcentagem) {
        this.porcentagem = porcentagem;
    }

    public boolean isProprietarioPrincipal() {
        return proprietarioPrincipal;
    }

    public void setProprietarioPrincipal(boolean proprietarioPrincipal) {
        this.proprietarioPrincipal = proprietarioPrincipal;
    }
}
