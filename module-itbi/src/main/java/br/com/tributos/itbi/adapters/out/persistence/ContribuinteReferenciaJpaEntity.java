package br.com.tributos.itbi.adapters.out.persistence;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "ItbiContribuinteReferencia")
@Table(name = "iss_contribuinte")
public class ContribuinteReferenciaJpaEntity {

    @Id
    private UUID id;

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
