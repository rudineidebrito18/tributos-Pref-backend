package br.com.tributos.identity.application;

import java.util.Set;

import org.springframework.stereotype.Component;

import br.com.tributos.identity.application.ports.GeradorToken;
import br.com.tributos.identity.domain.RefreshTokenRepository;
import br.com.tributos.identity.domain.Usuario;
import br.com.tributos.identity.domain.UsuarioRepository;

/**
 * Emite um novo par access+refresh token para um usuário já autenticado — passo final,
 * comum a três fluxos: login sem MFA, confirmação do desafio MFA, e renovação de sessão.
 * Extraído para não repetir "buscar papéis + gerar JWT + persistir refresh token" três
 * vezes.
 */
@Component
class EmissorDeTokens {

    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final GeradorToken geradorToken;

    EmissorDeTokens(UsuarioRepository usuarioRepository, RefreshTokenRepository refreshTokenRepository, GeradorToken geradorToken) {
        this.usuarioRepository = usuarioRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.geradorToken = geradorToken;
    }

    TokensDeAcesso emitirPara(Usuario usuario) {
        Set<String> papeis = usuarioRepository.buscarNomesDosPapeis(usuario.getId());
        String accessToken = geradorToken.gerarAccessToken(usuario, papeis);

        RefreshTokenFactory.Emitido refresh = RefreshTokenFactory.emitir(usuario.getId(), usuario.getTenantId());
        refreshTokenRepository.salvar(refresh.entidade());

        return TokensDeAcesso.de(accessToken, refresh.valorPlano(), geradorToken.duracaoAccessToken().toSeconds());
    }
}
