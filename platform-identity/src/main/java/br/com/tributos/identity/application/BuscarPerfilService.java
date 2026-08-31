package br.com.tributos.identity.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.identity.domain.Usuario;
import br.com.tributos.identity.domain.UsuarioRepository;
import br.com.tributos.kernel.exception.NotFoundException;

@Service
public class BuscarPerfilService {

    private final UsuarioRepository usuarioRepository;

    public BuscarPerfilService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public Usuario executar(UUID usuarioId) {
        return usuarioRepository.buscarPorId(usuarioId)
            .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
    }
}
