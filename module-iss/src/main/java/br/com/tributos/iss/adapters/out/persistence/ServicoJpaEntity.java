package br.com.tributos.iss.adapters.out.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "iss_servico")
public class ServicoJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "codigo_lc116", nullable = false, length = 10)
    private String codigoLc116;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Column(name = "aliquota_minima", precision = 8, scale = 4)
    private BigDecimal aliquotaMinima;

    @Column(name = "aliquota_maxima", precision = 8, scale = 4)
    private BigDecimal aliquotaMaxima;

    @Column(nullable = false)
    private boolean ativo;

    @Column(name = "criado_em", nullable = false, updatable = false, insertable = false)
    private Instant criadoEm;

    protected ServicoJpaEntity() {
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

    public String getCodigoLc116() {
        return codigoLc116;
    }

    public void setCodigoLc116(String codigoLc116) {
        this.codigoLc116 = codigoLc116;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getAliquotaMinima() {
        return aliquotaMinima;
    }

    public void setAliquotaMinima(BigDecimal aliquotaMinima) {
        this.aliquotaMinima = aliquotaMinima;
    }

    public BigDecimal getAliquotaMaxima() {
        return aliquotaMaxima;
    }

    public void setAliquotaMaxima(BigDecimal aliquotaMaxima) {
        this.aliquotaMaxima = aliquotaMaxima;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
