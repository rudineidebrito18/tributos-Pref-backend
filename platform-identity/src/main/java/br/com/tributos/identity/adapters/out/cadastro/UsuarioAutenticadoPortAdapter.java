package br.com.tributos.identity.adapters.out.cadastro;

import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.cadastro.application.ports.UsuarioAutenticadoPort;
import br.com.tributos.identity.adapters.out.security.UsuarioAutenticadoResolver;
import br.com.tributos.kernel.exception.AutenticacaoException;

@Component
public class UsuarioAutenticadoPortAdapter implements UsuarioAutenticadoPort {

    private final UsuarioAutenticadoResolver usuarioAutenticadoResolver;

    public UsuarioAutenticadoPortAdapter(UsuarioAutenticadoResolver usuarioAutenticadoResolver) {
        this.usuarioAutenticadoResolver = usuarioAutenticadoResolver;
    }

    @Override
    public UUID usuarioIdAtualObrigatorio() {
        return usuarioAutenticadoResolver.usuarioIdAtual()
            .orElseThrow(() -> new AutenticacaoException("Usuário não autenticado."));
    }
}
