package br.com.tributos.iss.adapters.out.persistence;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "iss_tipo_solicitacao")
public class TipoSolicitacaoJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "usuario_notificar_id")
    private UUID usuarioNotificarId;

    @Column(nullable = false)
    private boolean ativo;

    protected TipoSolicitacaoJpaEntity() {
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public UUID getUsuarioNotificarId() {
        return usuarioNotificarId;
    }

    public void setUsuarioNotificarId(UUID usuarioNotificarId) {
        this.usuarioNotificarId = usuarioNotificarId;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
