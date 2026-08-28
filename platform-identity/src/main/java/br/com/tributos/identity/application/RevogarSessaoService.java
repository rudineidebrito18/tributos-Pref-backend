package br.com.tributos.identity.application;

import org.springframework.stereotype.Service;

import br.com.tributos.identity.domain.RefreshTokenRepository;

/**
 * Logout: revoga o refresh token apresentado. Idempotente de propósito — chamar de novo
 * com um token já revogado/inexistente não é erro, porque do ponto de vista do cliente o
 * resultado desejado ("não estar mais logado") já foi alcançado.
 */
@Service
public class RevogarSessaoService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RevogarSessaoService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void executar(String refreshTokenPlano) {
        refreshTokenRepository.buscarPorHash(RefreshTokenFactory.hash(refreshTokenPlano))
            .ifPresent(token -> {
                token.revogar();
                refreshTokenRepository.salvar(token);
            });
    }
}
