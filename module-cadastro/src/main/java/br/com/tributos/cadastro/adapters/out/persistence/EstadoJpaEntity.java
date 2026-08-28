package br.com.tributos.cadastro.adapters.out.persistence;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "estado")
public class EstadoJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 2)
    private String sigla;

    @Column(nullable = false)
    private String nome;

    public UUID getId() {
        return id;
    }

    public String getSigla() {
        return sigla;
    }

    public String getNome() {
        return nome;
    }
}
