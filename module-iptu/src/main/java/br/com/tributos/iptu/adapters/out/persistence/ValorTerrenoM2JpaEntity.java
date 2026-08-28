package br.com.tributos.iptu.adapters.out.persistence;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "iptu_valor_terreno_m2")
public class ValorTerrenoM2JpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "zona_fiscal_id", nullable = false)
    private UUID zonaFiscalId;

    @Column(nullable = false)
    private int exercicio;

    @Column(name = "valor_m2", nullable = false, precision = 14, scale = 2)
    private BigDecimal valorM2;

    protected ValorTerrenoM2JpaEntity() {
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

    public UUID getZonaFiscalId() {
        return zonaFiscalId;
    }

    public void setZonaFiscalId(UUID zonaFiscalId) {
        this.zonaFiscalId = zonaFiscalId;
    }

    public int getExercicio() {
        return exercicio;
    }

    public void setExercicio(int exercicio) {
        this.exercicio = exercicio;
    }

    public BigDecimal getValorM2() {
        return valorM2;
    }

    public void setValorM2(BigDecimal valorM2) {
        this.valorM2 = valorM2;
    }
}
