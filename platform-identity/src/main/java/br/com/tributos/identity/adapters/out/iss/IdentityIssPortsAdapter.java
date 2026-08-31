package br.com.tributos.identity.adapters.out.iss;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.identity.adapters.out.security.UsuarioAutenticadoResolver;
import br.com.tributos.identity.application.EnviarMensagemService;
import br.com.tributos.kernel.exception.AutenticacaoException;
import br.com.tributos.kernel.identity.MensageriaInternaPort;
import br.com.tributos.kernel.identity.UsuarioAutenticadoPort;

@Component
public class IdentityIssPortsAdapter implements UsuarioAutenticadoPort, MensageriaInternaPort {

    private final UsuarioAutenticadoResolver usuarioAutenticadoResolver;
    private final EnviarMensagemService enviarMensagemService;

    public IdentityIssPortsAdapter(
        UsuarioAutenticadoResolver usuarioAutenticadoResolver,
        EnviarMensagemService enviarMensagemService
    ) {
        this.usuarioAutenticadoResolver = usuarioAutenticadoResolver;
        this.enviarMensagemService = enviarMensagemService;
    }

    @Override
    public UUID usuarioIdAtualObrigatorio() {
        return usuarioAutenticadoResolver.usuarioIdAtual()
            .orElseThrow(() -> new AutenticacaoException("Usuário não autenticado."));
    }

    @Override
    public void enviar(UUID destinatarioId, String assunto, String corpo) {
        enviarMensagemService.executar(assunto, corpo, List.of(destinatarioId));
    }
}
