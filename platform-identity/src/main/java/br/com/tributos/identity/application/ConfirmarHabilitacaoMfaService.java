package br.com.tributos.identity.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.tributos.identity.domain.Usuario;
import br.com.tributos.identity.domain.UsuarioRepository;
import br.com.tributos.kernel.exception.AutenticacaoException;
import br.com.tributos.kernel.exception.NotFoundException;

@Service
public class ConfirmarHabilitacaoMfaService {

    private final UsuarioRepository usuarioRepository;
    private final ValidadorMfaPorTipo validadorMfaPorTipo;

    public ConfirmarHabilitacaoMfaService(
        UsuarioRepository usuarioRepository,
        ValidadorMfaPorTipo validadorMfaPorTipo
    ) {
        this.usuarioRepository = usuarioRepository;
        this.validadorMfaPorTipo = validadorMfaPorTipo;
    }

    public void executar(UUID usuarioId, String codigo) {
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
            .orElseThrow(() -> NotFoundException.de("Usuário", usuarioId));

        if (!validadorMfaPorTipo.validar(usuario, codigo)) {
            throw new AutenticacaoException("Código de verificação inválido.");
        }

        usuario.confirmarHabilitacaoMfa();
        usuarioRepository.salvar(usuario);
    }
}
