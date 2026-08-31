package br.com.tributos.identity.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.com.tributos.cadastro.application.AnexarDocumentoInstitucionalService;
import br.com.tributos.cadastro.domain.Documento;
import br.com.tributos.identity.domain.Usuario;
import br.com.tributos.identity.domain.UsuarioRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class AnexarFotoPerfilService {

    private static final String TIPO_FOTO_PERFIL = "FOTO_PERFIL";

    private final AnexarDocumentoInstitucionalService anexarDocumentoInstitucionalService;
    private final UsuarioRepository usuarioRepository;

    public AnexarFotoPerfilService(
        AnexarDocumentoInstitucionalService anexarDocumentoInstitucionalService,
        UsuarioRepository usuarioRepository
    ) {
        this.anexarDocumentoInstitucionalService = anexarDocumentoInstitucionalService;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Usuario executar(UUID usuarioId, MultipartFile arquivo) {
        UUID tenantId = TenantContext.getObrigatorio();
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
            .filter(u -> u.getTenantId().equals(tenantId))
            .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

        Documento foto = anexarDocumentoInstitucionalService.executar(TIPO_FOTO_PERFIL, arquivo);
        usuario.definirFotoDocumentoId(foto.id());
        usuarioRepository.salvar(usuario);
        return usuario;
    }
}
