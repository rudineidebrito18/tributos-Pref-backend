package br.com.tributos.identity.adapters.out.persistence;

import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @ElementCollection
    @CollectionTable(name = "tenant_modulo_ativo", joinColumns = @jakarta.persistence.JoinColumn(name = "tenant_id"))
    @Column(name = "modulo_id")
    private Set<String> modulosAtivos;

    @Column(nullable = false)
    private boolean ativo;

    protected TenantJpaEntity() {
        // exigido pelo JPA
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
