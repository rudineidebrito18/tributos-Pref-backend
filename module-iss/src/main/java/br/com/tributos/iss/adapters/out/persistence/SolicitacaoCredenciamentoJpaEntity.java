package br.com.tributos.iss.adapters.out.persistence;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "iss_solicitacao_credenciamento")
public class SolicitacaoCredenciamentoJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "contribuinte_id", nullable = false)
    private UUID contribuinteId;

    @Column(name = "status_id", nullable = false)
    private UUID statusId;

    private String observacao;

    @Column(name = "analisado_por")
    private UUID analisadoPor;

    @Column(name = "analisado_em")
    private Instant analisadoEm;

    @Column(name = "criado_em", nullable = false, updatable = false, insertable = false)
    private Instant criadoEm;

    protected SolicitacaoCredenciamentoJpaEntity() {
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

    public UUID getStatusId() {
        return statusId;
    }

    public void setStatusId(UUID statusId) {
        this.statusId = statusId;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public UUID getAnalisadoPor() {
        return analisadoPor;
    }

    public void setAnalisadoPor(UUID analisadoPor) {
        this.analisadoPor = analisadoPor;
    }

    public Instant getAnalisadoEm() {
        return analisadoEm;
    }

    public void setAnalisadoEm(Instant analisadoEm) {
        this.analisadoEm = analisadoEm;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
