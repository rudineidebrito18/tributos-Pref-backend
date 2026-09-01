package br.com.tributos.iptu.adapters.out.persistence;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "IptuUsuarioReferencia")
@Table(name = "usuario")
public class UsuarioReferenciaJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false, insertable = false, updatable = false)
    private UUID tenantId;

    @Column(nullable = false, insertable = false, updatable = false)
    private String login;

    protected UsuarioReferenciaJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }
}
