package br.com.tributos.cadastro.adapters.out.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bairro")
public class BairroJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "cidade_id", nullable = false)
    private UUID cidadeId;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(name = "zona_fiscal_id")
    private UUID zonaFiscalId;

    @Column(name = "valor_terreno", precision = 14, scale = 2)
    private BigDecimal valorTerreno;

    @Column(name = "criado_em", nullable = false, updatable = false, insertable = false)
    private Instant criadoEm;

    protected BairroJpaEntity() {
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

    public UUID getCidadeId() {
        return cidadeId;
    }

    public void setCidadeId(UUID cidadeId) {
        this.cidadeId = cidadeId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public UUID getZonaFiscalId() {
        return zonaFiscalId;
    }

    public void setZonaFiscalId(UUID zonaFiscalId) {
        this.zonaFiscalId = zonaFiscalId;
    }

    public BigDecimal getValorTerreno() {
        return valorTerreno;
    }

    public void setValorTerreno(BigDecimal valorTerreno) {
        this.valorTerreno = valorTerreno;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
