package br.com.tributos.cadastro.adapters.out.persistence;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cidade")
public class CidadeJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoJpaEntity estado;

    @Column(nullable = false)
    private String nome;

    @Column(name = "codigo_ibge")
    private String codigoIbge;

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public EstadoJpaEntity getEstado() {
        return estado;
    }

    public String getCodigoIbge() {
        return codigoIbge;
    }
}
