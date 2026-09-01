package br.com.tributos.identity.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.tributos.identity.application.ports.GeradorToken;
import br.com.tributos.identity.domain.TipoMfa;
import br.com.tributos.identity.domain.Usuario;
import br.com.tributos.identity.domain.UsuarioRepository;
import br.com.tributos.kernel.exception.AutenticacaoException;

/** Segunda etapa do login: troca o token de desafio + código MFA pelos tokens finais. */
@Service
public class ConfirmarDesafioMfaService {

    private final UsuarioRepository usuarioRepository;
    private final GeradorToken geradorToken;
    private final ValidadorMfaPorTipo validadorMfaPorTipo;
    private final EmissorDeTokens emissorDeTokens;

    public ConfirmarDesafioMfaService(
        UsuarioRepository usuarioRepository,
        GeradorToken geradorToken,
        ValidadorMfaPorTipo validadorMfaPorTipo,
        EmissorDeTokens emissorDeTokens
    ) {
        this.usuarioRepository = usuarioRepository;
        this.geradorToken = geradorToken;
        this.validadorMfaPorTipo = validadorMfaPorTipo;
        this.emissorDeTokens = emissorDeTokens;
    }

    public TokensDeAcesso executar(String tokenMfaPendente, String codigo) {
        UUID usuarioId = geradorToken.validarTokenMfaPendente(tokenMfaPendente);

        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
            .filter(Usuario::isAtivo)
            .orElseThrow(() -> new AutenticacaoException("Sessão de verificação inválida — faça login novamente."));

        if (!usuario.isMfaHabilitado() || !validadorMfaPorTipo.validar(usuario, codigo)) {
            throw new AutenticacaoException("Código de verificação inválido.");
        }

        if (usuario.getMfaTipo() == TipoMfa.EMAIL) {
            usuario.limparDesafioEmailPendente();
            usuarioRepository.salvar(usuario);
        }

        return emissorDeTokens.emitirPara(usuario);
    }
}
