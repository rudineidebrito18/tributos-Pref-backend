package br.com.tributos.identity.application;

import java.time.Instant;

import org.springframework.stereotype.Service;

import br.com.tributos.identity.domain.RefreshToken;
import br.com.tributos.identity.domain.RefreshTokenRepository;
import br.com.tributos.identity.domain.Usuario;
import br.com.tributos.identity.domain.UsuarioRepository;
import br.com.tributos.kernel.exception.AutenticacaoException;

/**
 * Troca um refresh token válido por um novo par de tokens — com rotação: o refresh usado
 * é imediatamente revogado, então ele só pode ser "gasto" uma vez. Se um refresh já
 * revogado for apresentado de novo, é sinal de token roubado sendo reutilizado (o cliente
 * legítimo já rotacionou); nesta fase apenas rejeitamos a chamada — bloquear todas as
 * sessões do usuário nesse cenário é uma melhoria de segurança para uma fase futura.
 */
@Service
public class RenovarTokenService {

    private static final String MENSAGEM_SESSAO_EXPIRADA = "Sessão expirada — faça login novamente.";

    private final RefreshTokenRepository refreshTokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmissorDeTokens emissorDeTokens;

    public RenovarTokenService(RefreshTokenRepository refreshTokenRepository, UsuarioRepository usuarioRepository, EmissorDeTokens emissorDeTokens) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.usuarioRepository = usuarioRepository;
        this.emissorDeTokens = emissorDeTokens;
    }

    public TokensDeAcesso executar(String refreshTokenPlano) {
        RefreshToken tokenAtual = refreshTokenRepository.buscarPorHash(RefreshTokenFactory.hash(refreshTokenPlano))
            .filter(token -> token.valido(Instant.now()))
            .orElseThrow(() -> new AutenticacaoException(MENSAGEM_SESSAO_EXPIRADA));

        tokenAtual.revogar();
        refreshTokenRepository.salvar(tokenAtual);

        Usuario usuario = usuarioRepository.buscarPorId(tokenAtual.getUsuarioId())
            .filter(Usuario::isAtivo)
            .orElseThrow(() -> new AutenticacaoException(MENSAGEM_SESSAO_EXPIRADA));

        return emissorDeTokens.emitirPara(usuario);
    }
}
