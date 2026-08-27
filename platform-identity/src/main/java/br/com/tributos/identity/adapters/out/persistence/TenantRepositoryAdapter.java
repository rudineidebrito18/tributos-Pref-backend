package br.com.tributos.identity.adapters.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.tributos.identity.domain.PaletaTenant;
import br.com.tributos.identity.domain.Tenant;
import br.com.tributos.identity.domain.TenantRepository;

/**
 * Implementação da porta {@link TenantRepository} usando Spring Data JPA. Único ponto do
 * módulo que traduz entre a entidade de persistência ({@link TenantJpaEntity}) e o objeto
 * de domínio ({@link Tenant}) — o resto da aplicação nunca vê {@link TenantJpaEntity}.
 */
@Component
public class TenantRepositoryAdapter implements TenantRepository {

    private final TenantJpaRepository jpaRepository;

    public TenantRepositoryAdapter(TenantJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Tenant> buscarPorSlug(String slug) {
        return jpaRepository.findBySlug(slug).map(TenantRepositoryAdapter::paraDominio);
    }

    private static Tenant paraDominio(TenantJpaEntity entidade) {
        PaletaTenant paleta = new PaletaTenant(
            entidade.getCorAccent(),
            entidade.getCorAccentDark(),
            entidade.getCorAccentSecondary(),
            entidade.getCorAccentTertiary()
        );

        return new Tenant(
            entidade.getId(),
            entidade.getSlug(),
            entidade.getNome(),
            entidade.getUf(),
            entidade.getTipoEntidade(),
            entidade.getLogoUrl(),
            paleta,
            entidade.getModulosAtivos(),
            entidade.isAtivo()
        );
    }
}
