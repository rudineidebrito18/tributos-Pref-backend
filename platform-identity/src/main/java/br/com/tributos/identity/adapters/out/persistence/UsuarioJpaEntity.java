package br.com.tributos.identity.adapters.out.persistence;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import br.com.tributos.identity.domain.TipoMfa;

@Entity
@Table(name = "usuario")
public class UsuarioJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private String login;

    @Column(nullable = false)
    private String email;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Column(name = "mfa_habilitado", nullable = false)
    private boolean mfaHabilitado;

    @Enumerated(EnumType.STRING)
    @Column(name = "mfa_tipo")
    private TipoMfa mfaTipo;

    @Column(name = "mfa_secret")
    private String mfaSecret;

    @Column(nullable = false)
    private boolean ativo;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_papel",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "papel_id")
    )
    private Set<PapelJpaEntity> papeis = new HashSet<>();

    protected UsuarioJpaEntity() {
    }

    public UsuarioJpaEntity(
        UUID id, UUID tenantId, String login, String email, String senhaHash,
        boolean mfaHabilitado, TipoMfa mfaTipo, String mfaSecret, boolean ativo
    ) {
        this.id = id;
        this.tenantId = tenantId;
        this.login = login;
        this.email = email;
        this.senhaHash = senhaHash;
        this.mfaHabilitado = mfaHabilitado;
        this.mfaTipo = mfaTipo;
        this.mfaSecret = mfaSecret;
        this.ativo = ativo;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public boolean isMfaHabilitado() {
        return mfaHabilitado;
    }

    public void setMfaHabilitado(boolean mfaHabilitado) {
        this.mfaHabilitado = mfaHabilitado;
    }

    public TipoMfa getMfaTipo() {
        return mfaTipo;
    }

    public void setMfaTipo(TipoMfa mfaTipo) {
        this.mfaTipo = mfaTipo;
    }

    public String getMfaSecret() {
        return mfaSecret;
    }

    public void setMfaSecret(String mfaSecret) {
        this.mfaSecret = mfaSecret;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public Set<PapelJpaEntity> getPapeis() {
        return papeis;
    }
}
