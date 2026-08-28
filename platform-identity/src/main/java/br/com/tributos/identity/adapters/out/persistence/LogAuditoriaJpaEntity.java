package br.com.tributos.identity.adapters.out.persistence;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "log_auditoria")
public class LogAuditoriaJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(nullable = false, length = 100)
    private String entidade;

    @Column(name = "entidade_id", length = 100)
    private String entidadeId;

    @Column(nullable = false, length = 50)
    private String acao;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "dados_antes", columnDefinition = "jsonb")
    private Object dadosAntes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "dados_depois", columnDefinition = "jsonb")
    private Object dadosDepois;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    protected LogAuditoriaJpaEntity() {
    }

    public static LogAuditoriaJpaEntity nova() {
        return new LogAuditoriaJpaEntity();
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

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getEntidade() {
        return entidade;
    }

    public void setEntidade(String entidade) {
        this.entidade = entidade;
    }

    public String getEntidadeId() {
        return entidadeId;
    }

    public void setEntidadeId(String entidadeId) {
        this.entidadeId = entidadeId;
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public Object getDadosAntes() {
        return dadosAntes;
    }

    public void setDadosAntes(Object dadosAntes) {
        this.dadosAntes = dadosAntes;
    }

    public Object getDadosDepois() {
        return dadosDepois;
    }

    public void setDadosDepois(Object dadosDepois) {
        this.dadosDepois = dadosDepois;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Instant criadoEm) {
        this.criadoEm = criadoEm;
    }
}
