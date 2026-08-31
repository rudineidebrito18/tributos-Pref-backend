package br.com.tributos.cadastro.application;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.cadastro.domain.Documento;
import br.com.tributos.cadastro.domain.DocumentoRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class ListarDocumentosSistemaService {

    private final DocumentoRepository documentoRepository;

    public ListarDocumentosSistemaService(DocumentoRepository documentoRepository) {
        this.documentoRepository = documentoRepository;
    }

    @Transactional(readOnly = true)
    public Page<Documento> executar(String titulo, UUID categoriaId, String nomeArquivo, Pageable pageable) {
        UUID tenantId = TenantContext.getObrigatorio();
        return documentoRepository.listarSistema(tenantId, titulo, categoriaId, nomeArquivo, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Documento> listarCompartilhadosComigo(
        UUID usuarioId,
        String titulo,
        UUID categoriaId,
        String nomeArquivo,
        Pageable pageable
    ) {
        UUID tenantId = TenantContext.getObrigatorio();
        return documentoRepository.listarCompartilhadosComUsuario(
            tenantId, usuarioId, titulo, categoriaId, nomeArquivo, pageable
        );
    }
}
