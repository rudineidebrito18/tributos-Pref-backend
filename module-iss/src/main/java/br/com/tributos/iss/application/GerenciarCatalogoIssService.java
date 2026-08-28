package br.com.tributos.iss.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iss.domain.CatalogoIss;
import br.com.tributos.iss.domain.CatalogoIssRepository;
import br.com.tributos.iss.domain.TipoCatalogoIss;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class GerenciarCatalogoIssService {

    private final CatalogoIssRepository catalogoIssRepository;

    public GerenciarCatalogoIssService(CatalogoIssRepository catalogoIssRepository) {
        this.catalogoIssRepository = catalogoIssRepository;
    }

    @Transactional(readOnly = true)
    public List<CatalogoIss> listar(TipoCatalogoIss tipo) {
        return catalogoIssRepository.listar(tipo);
    }

    @Transactional(readOnly = true)
    public CatalogoIss buscar(TipoCatalogoIss tipo, UUID id) {
        return catalogoIssRepository.buscarPorId(tipo, id)
            .orElseThrow(() -> new NotFoundException("Item de catálogo não encontrado."));
    }

    @Transactional
    public CatalogoIss criar(TipoCatalogoIss tipo, String nome, boolean ativo) {
        validarNome(nome);
        if (catalogoIssRepository.existePorNome(tipo, nome.trim(), null)) {
            throw new ValidationException("Já existe um item com este nome no catálogo.");
        }
        UUID tenantId = TenantContext.getObrigatorio();
        CatalogoIss item = new CatalogoIss(UUID.randomUUID(), tenantId, nome.trim(), ativo);
        return catalogoIssRepository.salvar(tipo, item);
    }

    @Transactional
    public CatalogoIss atualizar(TipoCatalogoIss tipo, UUID id, String nome, boolean ativo) {
        validarNome(nome);
        CatalogoIss existente = buscar(tipo, id);
        if (catalogoIssRepository.existePorNome(tipo, nome.trim(), id)) {
            throw new ValidationException("Já existe um item com este nome no catálogo.");
        }
        CatalogoIss atualizado = new CatalogoIss(existente.id(), existente.tenantId(), nome.trim(), ativo);
        return catalogoIssRepository.salvar(tipo, atualizado);
    }

    @Transactional
    public void excluir(TipoCatalogoIss tipo, UUID id) {
        if (catalogoIssRepository.buscarPorId(tipo, id).isEmpty()) {
            throw new NotFoundException("Item de catálogo não encontrado.");
        }
        catalogoIssRepository.excluir(tipo, id);
    }

    private static void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new ValidationException("Informe o nome do item de catálogo.");
        }
    }
}
