package br.com.tributos.identity.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.tributos.identity.application.ports.VerificadorMfa;
import br.com.tributos.identity.domain.Usuario;
import br.com.tributos.identity.domain.UsuarioRepository;
import br.com.tributos.kernel.exception.NotFoundException;

/**
 * Início do fluxo de habilitação de MFA (usuário já autenticado). Gera o segredo e grava
 * no usuário, mas {@code mfaHabilitado} só vira {@code true} em
 * {@link ConfirmarHabilitacaoMfaService} — ver comentário em {@link Usuario#confirmarHabilitacaoMfa()}.
 */
@Service
public class HabilitarMfaService {

    private final UsuarioRepository usuarioRepository;
    private final VerificadorMfa verificadorMfa;

    public HabilitarMfaService(UsuarioRepository usuarioRepository, VerificadorMfa verificadorMfa) {
        this.usuarioRepository = usuarioRepository;
        this.verificadorMfa = verificadorMfa;
    }

    public SegredoMfaGerado executar(UUID usuarioId) {
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
            .orElseThrow(() -> NotFoundException.de("Usuário", usuarioId));

        String segredo = verificadorMfa.gerarSegredo();
        usuario.iniciarHabilitacaoMfa(segredo);
        usuarioRepository.salvar(usuario);

        return new SegredoMfaGerado(segredo, verificadorMfa.gerarUriProvisionamento(segredo, usuario.getEmail()));
    }
}
