package br.com.tributos.identity.domain;

import java.util.Optional;

public interface RefreshTokenRepository {

    void salvar(RefreshToken refreshToken);

    Optional<RefreshToken> buscarPorHash(String tokenHash);
}
