package br.com.tributos.identity.domain;

import java.util.Set;
import java.util.UUID;

/**
 * Uma prefeitura ou câmara municipal cliente da plataforma. Entidade de domínio pura —
 * sem anotação JPA (essas vivem em {@code adapters.out.persistence.TenantJpaEntity}) — e
 * carrega exatamente o que o endpoint público de branding expõe ao frontend, mais o que a
 * plataforma precisa internamente ({@code ativo}).
 */
public final class Tenant {

    private final UUID id;
    private final String slug;
    private String nome;
    private String uf;
    private TipoEntidade tipoEntidade;
    private String logoUrl;
    private PaletaTenant paleta;
    private Set<String> modulosAtivos;
    private boolean ativo;

    public Tenant(
        UUID id,
        String slug,
        String nome,
        String uf,
        TipoEntidade tipoEntidade,
        String logoUrl,
        PaletaTenant paleta,
        Set<String> modulosAtivos,
        boolean ativo
    ) {
        this.id = id;
        this.slug = slug;
        this.nome = nome;
        this.uf = uf;
        this.tipoEntidade = tipoEntidade;
        this.logoUrl = logoUrl;
        this.paleta = paleta;
        this.modulosAtivos = modulosAtivos;
        this.ativo = ativo;
    }

    public UUID getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getNome() {
        return nome;
    }

    public String getUf() {
        return uf;
    }

    public TipoEntidade getTipoEntidade() {
        return tipoEntidade;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public PaletaTenant getPaleta() {
        return paleta;
    }

    public Set<String> getModulosAtivos() {
        return modulosAtivos;
    }

    public boolean isAtivo() {
        return ativo;
    }
}
