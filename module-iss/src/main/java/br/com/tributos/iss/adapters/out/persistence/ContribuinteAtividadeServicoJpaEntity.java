package br.com.tributos.iss.adapters.out.persistence;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "iss_contribuinte_atividade_servico")
public class ContribuinteAtividadeServicoJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "contribuinte_id", nullable = false)
    private UUID contribuinteId;

    @Column(name = "atividade_id", nullable = false)
    private UUID atividadeId;

    @Column(name = "servico_id", nullable = false)
    private UUID servicoId;

    @Column(nullable = false)
    private boolean tributavel;

    @Column(name = "criado_em", nullable = false, updatable = false, insertable = false)
    private Instant criadoEm;

    protected ContribuinteAtividadeServicoJpaEntity() {
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

    public UUID getContribuinteId() {
        return contribuinteId;
    }

    public void setContribuinteId(UUID contribuinteId) {
        this.contribuinteId = contribuinteId;
    }

    public UUID getAtividadeId() {
        return atividadeId;
    }

    public void setAtividadeId(UUID atividadeId) {
        this.atividadeId = atividadeId;
    }

    public UUID getServicoId() {
        return servicoId;
    }

    public void setServicoId(UUID servicoId) {
        this.servicoId = servicoId;
    }

    public boolean isTributavel() {
        return tributavel;
    }

    public void setTributavel(boolean tributavel) {
        this.tributavel = tributavel;
    }
}
