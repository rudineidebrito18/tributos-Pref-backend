package br.com.tributos.cadastro.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.cadastro.domain.DocumentoCategoria;
import br.com.tributos.cadastro.domain.DocumentoCategoriaRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class GerenciarDocumentoCategoriaService {

    private final DocumentoCategoriaRepository documentoCategoriaRepository;

    public GerenciarDocumentoCategoriaService(DocumentoCategoriaRepository documentoCategoriaRepository) {
        this.documentoCategoriaRepository = documentoCategoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<DocumentoCategoria> listar() {
        return documentoCategoriaRepository.listar();
    }

    @Transactional(readOnly = true)
    public DocumentoCategoria buscar(UUID id) {
        return documentoCategoriaRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Categoria de documento não encontrada."));
    }

    @Transactional
    public DocumentoCategoria criar(String nome) {
        validarNome(nome);
        String nomeNormalizado = nome.trim();
        if (documentoCategoriaRepository.existePorNome(nomeNormalizado, null)) {
            throw new ValidationException("Já existe uma categoria com este nome.");
        }
        UUID tenantId = TenantContext.getObrigatorio();
        return documentoCategoriaRepository.salvar(new DocumentoCategoria(
            UUID.randomUUID(), tenantId, nomeNormalizado, null
        ));
    }

    @Transactional
    public DocumentoCategoria atualizar(UUID id, String nome) {
        validarNome(nome);
        DocumentoCategoria existente = buscar(id);
        String nomeNormalizado = nome.trim();
        if (documentoCategoriaRepository.existePorNome(nomeNormalizado, id)) {
            throw new ValidationException("Já existe uma categoria com este nome.");
        }
        return documentoCategoriaRepository.salvar(new DocumentoCategoria(
            existente.id(), existente.tenantId(), nomeNormalizado, existente.criadoEm()
        ));
    }

    @Transactional
    public void excluir(UUID id) {
        if (documentoCategoriaRepository.buscarPorId(id).isEmpty()) {
            throw new NotFoundException("Categoria de documento não encontrada.");
        }
        documentoCategoriaRepository.excluir(id);
    }

    private static void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new ValidationException("Informe o nome da categoria.");
        }
    }
}
