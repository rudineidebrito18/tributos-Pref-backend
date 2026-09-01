package br.com.tributos.identity.adapters.out.persistence;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import java.time.Instant;

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

    @Column(length = 200)
    private String nome;

    @Column(nullable = false)
    private String login;

    @Column(nullable = false)
    private String email;

    @Column(name = "foto_documento_id")
    private UUID fotoDocumentoId;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Column(name = "mfa_habilitado", nullable = false)
    private boolean mfaHabilitado;

    @Enumerated(EnumType.STRING)
    @Column(name = "mfa_tipo")
    private TipoMfa mfaTipo;

    @Column(name = "mfa_secret")
    private String mfaSecret;

    @Column(name = "mfa_codigo_expira_em")
    private Instant mfaCodigoExpiraEm;

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
        UUID id, UUID tenantId, String nome, String login, String email, UUID fotoDocumentoId,
        String senhaHash, boolean mfaHabilitado, TipoMfa mfaTipo, String mfaSecret,
        Instant mfaCodigoExpiraEm, boolean ativo
    ) {
        this.id = id;
        this.tenantId = tenantId;
        this.nome = nome;
        this.login = login;
        this.email = email;
        this.fotoDocumentoId = fotoDocumentoId;
        this.senhaHash = senhaHash;
        this.mfaHabilitado = mfaHabilitado;
        this.mfaTipo = mfaTipo;
        this.mfaSecret = mfaSecret;
        this.mfaCodigoExpiraEm = mfaCodigoExpiraEm;
        this.ativo = ativo;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UUID getFotoDocumentoId() {
        return fotoDocumentoId;
    }

    public void setFotoDocumentoId(UUID fotoDocumentoId) {
        this.fotoDocumentoId = fotoDocumentoId;
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

    public Instant getMfaCodigoExpiraEm() {
        return mfaCodigoExpiraEm;
    }

    public void setMfaCodigoExpiraEm(Instant mfaCodigoExpiraEm) {
        this.mfaCodigoExpiraEm = mfaCodigoExpiraEm;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public Set<PapelJpaEntity> getPapeis() {
        return papeis;
    }
}
