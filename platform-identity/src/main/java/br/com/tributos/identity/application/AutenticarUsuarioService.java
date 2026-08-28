package br.com.tributos.identity.application;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.tributos.identity.application.ports.GeradorToken;
import br.com.tributos.identity.domain.Usuario;
import br.com.tributos.identity.domain.UsuarioRepository;
import br.com.tributos.kernel.exception.AutenticacaoException;

/**
 * Primeira etapa do login: valida login+senha. Se o usuário tem MFA habilitado, para por
 * aqui e devolve um token de desafio (ver {@link ResultadoLogin.DesafioMfaNecessario}) —
 * a segunda etapa é responsabilidade de {@link ConfirmarDesafioMfaService}.
 */
@Service
public class AutenticarUsuarioService {

    private static final String MENSAGEM_CREDENCIAIS_INVALIDAS = "Credenciais inválidas.";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final GeradorToken geradorToken;
    private final EmissorDeTokens emissorDeTokens;

    public AutenticarUsuarioService(
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder,
        GeradorToken geradorToken,
        EmissorDeTokens emissorDeTokens
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.geradorToken = geradorToken;
        this.emissorDeTokens = emissorDeTokens;
    }

    public ResultadoLogin executar(UUID tenantId, String login, String senha) {
        Usuario usuario = usuarioRepository.buscarPorLogin(tenantId, login)
            .filter(Usuario::isAtivo)
            // Mesma mensagem para "login não existe" e "senha errada" — não dar pista de
            // qual das duas falhou evita enumeração de contas válidas por tentativa e erro.
            .orElseThrow(() -> new AutenticacaoException(MENSAGEM_CREDENCIAIS_INVALIDAS));

        if (!passwordEncoder.matches(senha, usuario.getSenhaHash())) {
            throw new AutenticacaoException(MENSAGEM_CREDENCIAIS_INVALIDAS);
        }

        if (usuario.isMfaHabilitado()) {
            return new ResultadoLogin.DesafioMfaNecessario(geradorToken.gerarTokenMfaPendente(usuario.getId()));
        }

        return new ResultadoLogin.Autenticado(emissorDeTokens.emitirPara(usuario));
    }
}
