package br.com.tributos.cadastro.adapters.out.persistence;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "documento_compartilhamento")
public class DocumentoCompartilhamentoJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "documento_id", nullable = false)
    private UUID documentoId;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "criado_em", nullable = false, updatable = false, insertable = false)
    private Instant criadoEm;

    protected DocumentoCompartilhamentoJpaEntity() {
    }

    public DocumentoCompartilhamentoJpaEntity(UUID id, UUID tenantId, UUID documentoId, UUID usuarioId) {
        this.id = id;
        this.tenantId = tenantId;
        this.documentoId = documentoId;
        this.usuarioId = usuarioId;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public UUID getDocumentoId() {
        return documentoId;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
