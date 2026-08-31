package br.com.tributos.cadastro.adapters.out.persistence;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "logradouro")
public class LogradouroJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "cidade_id", nullable = false)
    private UUID cidadeId;

    @Column(name = "bairro_id")
    private UUID bairroId;

    @Column(length = 50)
    private String tipo;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(length = 8)
    private String cep;

    @Column(name = "criado_em", nullable = false, updatable = false, insertable = false)
    private Instant criadoEm;

    protected LogradouroJpaEntity() {
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

    public UUID getBairroId() {
        return bairroId;
    }

    public void setBairroId(UUID bairroId) {
        this.bairroId = bairroId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
