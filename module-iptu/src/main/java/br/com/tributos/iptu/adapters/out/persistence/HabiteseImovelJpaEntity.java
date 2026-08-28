package br.com.tributos.iptu.adapters.out.persistence;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "imovel_habitese")
public class HabiteseImovelJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "imovel_id", nullable = false)
    private UUID imovelId;

    @Column(name = "tipo_id", nullable = false)
    private UUID tipoId;

    @Column(nullable = false)
    private long numero;

    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @Column(name = "data_emissao_ts", nullable = false, insertable = false, updatable = false)
    private Instant dataEmissaoTs;

    protected HabiteseImovelJpaEntity() {
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

    public UUID getTipoId() {
        return tipoId;
    }

    public void setTipoId(UUID tipoId) {
        this.tipoId = tipoId;
    }

    public long getNumero() {
        return numero;
    }

    public void setNumero(long numero) {
        this.numero = numero;
    }

    public LocalDate getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(LocalDate dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public Instant getDataEmissaoTs() {
        return dataEmissaoTs;
    }
}
