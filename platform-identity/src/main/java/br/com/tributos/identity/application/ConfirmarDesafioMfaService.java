package br.com.tributos.identity.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.tributos.identity.application.ports.GeradorToken;
import br.com.tributos.identity.application.ports.VerificadorMfa;
import br.com.tributos.identity.domain.Usuario;
import br.com.tributos.identity.domain.UsuarioRepository;
import br.com.tributos.kernel.exception.AutenticacaoException;

/** Segunda etapa do login: troca o token de desafio + código TOTP pelos tokens finais. */
@Service
public class ConfirmarDesafioMfaService {

    private final UsuarioRepository usuarioRepository;
    private final GeradorToken geradorToken;
    private final VerificadorMfa verificadorMfa;
    private final EmissorDeTokens emissorDeTokens;

    public ConfirmarDesafioMfaService(
        UsuarioRepository usuarioRepository,
        GeradorToken geradorToken,
        VerificadorMfa verificadorMfa,
        EmissorDeTokens emissorDeTokens
    ) {
        this.usuarioRepository = usuarioRepository;
        this.geradorToken = geradorToken;
        this.verificadorMfa = verificadorMfa;
        this.emissorDeTokens = emissorDeTokens;
    }

    public TokensDeAcesso executar(String tokenMfaPendente, String codigo) {
        UUID usuarioId = geradorToken.validarTokenMfaPendente(tokenMfaPendente);

        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
            .filter(Usuario::isAtivo)
            .orElseThrow(() -> new AutenticacaoException("Sessão de verificação inválida — faça login novamente."));

        if (!usuario.isMfaHabilitado() || !verificadorMfa.validarCodigo(usuario.getMfaSecret(), codigo)) {
            throw new AutenticacaoException("Código de verificação inválido.");
        }

        return emissorDeTokens.emitirPara(usuario);
    }
}
