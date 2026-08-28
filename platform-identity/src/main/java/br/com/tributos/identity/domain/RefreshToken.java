package br.com.tributos.identity.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Sessão de refresh token — só o HASH (SHA-256) do token opaco é persistido, nunca o valor
 * em texto puro (mesmo princípio de senha: se o banco vazar, os tokens não são
 * reutilizáveis). Suporta rotação (um refresh só pode ser usado uma vez — ao renovar,
 * marca-se {@code revogadoEm} no antigo e cria-se um novo) e revogação explícita (logout).
 */
public final class RefreshToken {

    private final UUID id;
    private final UUID usuarioId;
    private final UUID tenantId;
    private final String tokenHash;
    private final Instant criadoEm;
    private final Instant expiraEm;
    private Instant revogadoEm;

    public RefreshToken(UUID id, UUID usuarioId, UUID tenantId, String tokenHash, Instant criadoEm, Instant expiraEm, Instant revogadoEm) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.tenantId = tenantId;
        this.tokenHash = tokenHash;
        this.criadoEm = criadoEm;
        this.expiraEm = expiraEm;
        this.revogadoEm = revogadoEm;
    }

    public static RefreshToken novo(UUID usuarioId, UUID tenantId, String tokenHash, Instant expiraEm) {
        return new RefreshToken(UUID.randomUUID(), usuarioId, tenantId, tokenHash, Instant.now(), expiraEm, null);
    }

    public boolean valido(Instant agora) {
        return revogadoEm == null && agora.isBefore(expiraEm);
    }

    public void revogar() {
        this.revogadoEm = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getExpiraEm() {
        return expiraEm;
    }

    public Instant getRevogadoEm() {
        return revogadoEm;
    }
}
