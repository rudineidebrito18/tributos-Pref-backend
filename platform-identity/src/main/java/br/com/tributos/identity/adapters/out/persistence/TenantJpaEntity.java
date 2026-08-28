package br.com.tributos.identity.adapters.out.persistence;

import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import br.com.tributos.identity.domain.TipoEntidade;

/**
 * Entidade JPA — mapeia a tabela {@code tenant} (ver
 * db/migration/platform/V1__core_platform.sql em app-bootstrap). Fica isolada em
 * {@code adapters.out.persistence} de propósito: o domínio ({@link br.com.tributos.identity.domain.Tenant})
 * nunca importa {@code jakarta.persistence}.
 */
@Entity
@Table(name = "tenant")
public class TenantJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, length = 2)
    private String uf;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_entidade", nullable = false)
    private TipoEntidade tipoEntidade;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "cor_accent")
    private String corAccent;

    @Column(name = "cor_accent_dark")
    private String corAccentDark;

    @Column(name = "cor_accent_secondary")
    private String corAccentSecondary;

    @Column(name = "cor_accent_tertiary")
    private String corAccentTertiary;

    // EAGER de propósito: TenantRepositoryAdapter.paraDominio() lê esta coleção FORA de um
    // escopo @Transactional (a transação do Spring Data já fechou quando o .map() do
    // Optional roda) — mesma razão de UsuarioJpaEntity.papeis usar EAGER. Sem isto, o
    // endpoint público de branding devolve 401 (não 500!): a LazyInitializationException
    // na serialização do JSON é relançada durante o forward para /error, que cai em
    // "anyRequest().authenticated()" do SecurityConfig e mascara o erro real.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tenant_modulo_ativo", joinColumns = @jakarta.persistence.JoinColumn(name = "tenant_id"))
    @Column(name = "modulo_id")
    private Set<String> modulosAtivos;

    @Column(nullable = false)
    private boolean ativo;

    protected TenantJpaEntity() {
        // exigido pelo JPA
    }

    public TenantJpaEntity(
        UUID id, String slug, String nome, String uf, TipoEntidade tipoEntidade, String logoUrl,
        String corAccent, String corAccentDark, String corAccentSecondary, String corAccentTertiary,
        Set<String> modulosAtivos, boolean ativo
    ) {
        this.id = id;
        this.slug = slug;
        this.nome = nome;
        this.uf = uf;
        this.tipoEntidade = tipoEntidade;
        this.logoUrl = logoUrl;
        this.corAccent = corAccent;
        this.corAccentDark = corAccentDark;
        this.corAccentSecondary = corAccentSecondary;
        this.corAccentTertiary = corAccentTertiary;
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

    public String getCorAccent() {
        return corAccent;
    }

    public String getCorAccentDark() {
        return corAccentDark;
    }

    public String getCorAccentSecondary() {
        return corAccentSecondary;
    }

    public String getCorAccentTertiary() {
        return corAccentTertiary;
    }

    public Set<String> getModulosAtivos() {
        return modulosAtivos;
    }

    public boolean isAtivo() {
        return ativo;
    }
}
