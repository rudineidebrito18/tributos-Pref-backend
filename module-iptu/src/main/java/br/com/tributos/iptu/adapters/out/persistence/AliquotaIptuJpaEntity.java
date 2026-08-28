package br.com.tributos.iptu.adapters.out.persistence;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "iptu_aliquota")
public class AliquotaIptuJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private int exercicio;

    @Column(name = "destinacao_id", nullable = false)
    private UUID destinacaoId;

    @Column(name = "zona_fiscal_id", nullable = false)
    private UUID zonaFiscalId;

    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal aliquota;

    protected AliquotaIptuJpaEntity() {
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

    public int getExercicio() {
        return exercicio;
    }

    public void setExercicio(int exercicio) {
        this.exercicio = exercicio;
    }

    public UUID getDestinacaoId() {
        return destinacaoId;
    }

    public void setDestinacaoId(UUID destinacaoId) {
        this.destinacaoId = destinacaoId;
    }

    public UUID getZonaFiscalId() {
        return zonaFiscalId;
    }

    public void setZonaFiscalId(UUID zonaFiscalId) {
        this.zonaFiscalId = zonaFiscalId;
    }

    public BigDecimal getAliquota() {
        return aliquota;
    }

    public void setAliquota(BigDecimal aliquota) {
        this.aliquota = aliquota;
    }
}
