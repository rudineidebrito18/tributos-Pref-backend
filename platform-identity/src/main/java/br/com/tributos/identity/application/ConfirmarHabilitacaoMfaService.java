package br.com.tributos.identity.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.tributos.identity.application.ports.VerificadorMfa;
import br.com.tributos.identity.domain.Usuario;
import br.com.tributos.identity.domain.UsuarioRepository;
import br.com.tributos.kernel.exception.AutenticacaoException;
import br.com.tributos.kernel.exception.NotFoundException;

@Service
public class ConfirmarHabilitacaoMfaService {

    private final UsuarioRepository usuarioRepository;
    private final VerificadorMfa verificadorMfa;

    public ConfirmarHabilitacaoMfaService(UsuarioRepository usuarioRepository, VerificadorMfa verificadorMfa) {
        this.usuarioRepository = usuarioRepository;
        this.verificadorMfa = verificadorMfa;
    }

    public void executar(UUID usuarioId, String codigo) {
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
            .orElseThrow(() -> NotFoundException.de("Usuário", usuarioId));

        if (usuario.getMfaSecret() == null || !verificadorMfa.validarCodigo(usuario.getMfaSecret(), codigo)) {
            throw new AutenticacaoException("Código de verificação inválido.");
        }

        usuario.confirmarHabilitacaoMfa();
        usuarioRepository.salvar(usuario);
    }
}
