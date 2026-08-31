package br.com.tributos.cadastro.application;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.cadastro.domain.Documento;
import br.com.tributos.cadastro.domain.DocumentoCompartilhamento;
import br.com.tributos.cadastro.domain.DocumentoRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class CompartilharDocumentoService {

    private final DocumentoRepository documentoRepository;

    public CompartilharDocumentoService(DocumentoRepository documentoRepository) {
        this.documentoRepository = documentoRepository;
    }

    @Transactional
    public void executar(UUID documentoId, UUID usuarioDestinoId) {
        UUID tenantId = TenantContext.getObrigatorio();
        Documento documento = documentoRepository.buscarPorId(documentoId)
            .filter(d -> d.tenantId().equals(tenantId) && d.pessoaId() == null)
            .orElseThrow(() -> new NotFoundException("Documento não encontrado."));

        if (!documentoRepository.existeUsuarioAtivoNoTenant(tenantId, usuarioDestinoId)) {
            throw new ValidationException("Usuário de destino não encontrado ou inativo.");
        }

        if (documentoRepository.possuiCompartilhamento(documento.id(), usuarioDestinoId)) {
            throw new ValidationException("Documento já compartilhado com este usuário.");
        }

        documentoRepository.salvarCompartilhamento(new DocumentoCompartilhamento(
            UUID.randomUUID(), tenantId, documento.id(), usuarioDestinoId, Instant.now()
        ));
    }
}
