package br.com.tributos.iptu.adapters.out.persistence;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "IptuContribuinteReferencia")
@Table(name = "iss_contribuinte")
public class ContribuinteReferenciaJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false, insertable = false, updatable = false)
    private UUID tenantId;

    @Column(name = "pessoa_id", nullable = false, insertable = false, updatable = false)
    private UUID pessoaId;

    protected ContribuinteReferenciaJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public UUID getPessoaId() {
        return pessoaId;
    }
}
