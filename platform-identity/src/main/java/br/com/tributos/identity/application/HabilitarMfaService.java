package br.com.tributos.identity.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.tributos.identity.application.ports.VerificadorMfa;
import br.com.tributos.identity.domain.TipoMfa;
import br.com.tributos.identity.domain.Usuario;
import br.com.tributos.identity.domain.UsuarioRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;

/**
 * Início do fluxo de habilitação de MFA (usuário já autenticado). Gera o segredo TOTP ou
 * envia código por e-mail; {@code mfaHabilitado} só vira {@code true} em
 * {@link ConfirmarHabilitacaoMfaService}.
 */
@Service
public class HabilitarMfaService {

    private final UsuarioRepository usuarioRepository;
    private final VerificadorMfa verificadorMfa;
    private final GeradorCodigoMfaEmail geradorCodigoMfaEmail;

    public HabilitarMfaService(
        UsuarioRepository usuarioRepository,
        VerificadorMfa verificadorMfa,
        GeradorCodigoMfaEmail geradorCodigoMfaEmail
    ) {
        this.usuarioRepository = usuarioRepository;
        this.verificadorMfa = verificadorMfa;
        this.geradorCodigoMfaEmail = geradorCodigoMfaEmail;
    }

    public SegredoMfaGerado executar(UUID usuarioId, TipoMfa tipo) {
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
            .orElseThrow(() -> NotFoundException.de("Usuário", usuarioId));

        TipoMfa tipoEfetivo = tipo != null ? tipo : TipoMfa.TOTP;
        if (tipoEfetivo == TipoMfa.NENHUM) {
            throw new ValidationException("Informe TOTP ou EMAIL para habilitar MFA.");
        }

        if (tipoEfetivo == TipoMfa.EMAIL) {
            String codigo = geradorCodigoMfaEmail.gerarCodigo();
            geradorCodigoMfaEmail.registrarHabilitacao(usuario, codigo);
            usuarioRepository.salvar(usuario);
            return SegredoMfaGerado.email(geradorCodigoMfaEmail.mensagemEnvio(usuario.getEmail()));
        }

        String segredo = verificadorMfa.gerarSegredo();
        usuario.iniciarHabilitacaoMfaTotp(segredo);
        usuarioRepository.salvar(usuario);
        return SegredoMfaGerado.totp(segredo, verificadorMfa.gerarUriProvisionamento(segredo, usuario.getEmail()));
    }
}
