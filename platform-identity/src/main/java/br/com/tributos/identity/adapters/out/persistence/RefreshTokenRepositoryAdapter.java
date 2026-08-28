package br.com.tributos.identity.adapters.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.tributos.identity.domain.RefreshToken;
import br.com.tributos.identity.domain.RefreshTokenRepository;

@Component
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository jpaRepository;

    public RefreshTokenRepositoryAdapter(RefreshTokenJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void salvar(RefreshToken refreshToken) {
        jpaRepository.save(new RefreshTokenJpaEntity(
            refreshToken.getId(),
            refreshToken.getUsuarioId(),
            refreshToken.getTenantId(),
            refreshToken.getTokenHash(),
            refreshToken.getCriadoEm(),
            refreshToken.getExpiraEm(),
            refreshToken.getRevogadoEm()
        ));
    }

    @Override
    public Optional<RefreshToken> buscarPorHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash).map(entidade -> new RefreshToken(
            entidade.getId(),
            entidade.getUsuarioId(),
            entidade.getTenantId(),
            entidade.getTokenHash(),
            entidade.getCriadoEm(),
            entidade.getExpiraEm(),
            entidade.getRevogadoEm()
        ));
    }
}
