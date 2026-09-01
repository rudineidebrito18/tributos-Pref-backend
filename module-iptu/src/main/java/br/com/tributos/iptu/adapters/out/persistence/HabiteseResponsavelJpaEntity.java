package br.com.tributos.iptu.adapters.out.persistence;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "imovel_habitese_responsavel")
public class HabiteseResponsavelJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "habitese_id", nullable = false)
    private UUID habiteseId;

    @Column(nullable = false)
    private short ordem;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(length = 100)
    private String profissao;

    @Column(length = 50)
    private String documento;

    protected HabiteseResponsavelJpaEntity() {
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

    public UUID getHabiteseId() {
        return habiteseId;
    }

    public void setHabiteseId(UUID habiteseId) {
        this.habiteseId = habiteseId;
    }

    public short getOrdem() {
        return ordem;
    }

    public void setOrdem(short ordem) {
        this.ordem = ordem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getProfissao() {
        return profissao;
    }

    public void setProfissao(String profissao) {
        this.profissao = profissao;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }
}
