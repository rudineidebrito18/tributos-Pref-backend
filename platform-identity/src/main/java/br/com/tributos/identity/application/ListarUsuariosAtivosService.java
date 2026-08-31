package br.com.tributos.identity.application;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.tributos.identity.domain.Usuario;
import br.com.tributos.identity.domain.UsuarioRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class ListarUsuariosAtivosService {

    private final UsuarioRepository usuarioRepository;

    public ListarUsuariosAtivosService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> executar() {
        return usuarioRepository.listarAtivosDoTenant(TenantContext.getObrigatorio());
    }
}
