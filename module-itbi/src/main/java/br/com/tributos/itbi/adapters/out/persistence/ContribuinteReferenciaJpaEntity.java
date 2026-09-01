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

    protected ContribuinteReferenciaJpaEntity() {
    }
}
