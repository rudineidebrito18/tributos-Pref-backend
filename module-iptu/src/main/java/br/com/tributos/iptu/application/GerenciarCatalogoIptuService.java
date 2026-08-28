package br.com.tributos.iptu.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.iptu.domain.CatalogoIptu;
import br.com.tributos.iptu.domain.CatalogoIptuRepository;
import br.com.tributos.iptu.domain.TipoCatalogoIptu;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class GerenciarCatalogoIptuService {

    private final CatalogoIptuRepository catalogoIptuRepository;

    public GerenciarCatalogoIptuService(CatalogoIptuRepository catalogoIptuRepository) {
        this.catalogoIptuRepository = catalogoIptuRepository;
    }

    @Transactional(readOnly = true)
    public List<CatalogoIptu> listar(TipoCatalogoIptu tipo) {
        return catalogoIptuRepository.listar(tipo);
    }

    @Transactional(readOnly = true)
    public CatalogoIptu buscar(TipoCatalogoIptu tipo, UUID id) {
        return catalogoIptuRepository.buscarPorId(tipo, id)
            .orElseThrow(() -> new NotFoundException("Item de catálogo não encontrado."));
    }

    @Transactional
    public CatalogoIptu criar(TipoCatalogoIptu tipo, String nome, boolean ativo) {
        validarNome(nome);
        if (catalogoIptuRepository.existePorNome(tipo, nome.trim(), null)) {
            throw new ValidationException("Já existe um item com este nome no catálogo.");
        }
        UUID tenantId = TenantContext.getObrigatorio();
        CatalogoIptu item = new CatalogoIptu(UUID.randomUUID(), tenantId, nome.trim(), ativo);
        return catalogoIptuRepository.salvar(tipo, item);
    }

    @Transactional
    public CatalogoIptu atualizar(TipoCatalogoIptu tipo, UUID id, String nome, boolean ativo) {
        validarNome(nome);
        CatalogoIptu existente = buscar(tipo, id);
        if (catalogoIptuRepository.existePorNome(tipo, nome.trim(), id)) {
            throw new ValidationException("Já existe um item com este nome no catálogo.");
        }
        CatalogoIptu atualizado = new CatalogoIptu(existente.id(), existente.tenantId(), nome.trim(), ativo);
        return catalogoIptuRepository.salvar(tipo, atualizado);
    }

    @Transactional
    public void excluir(TipoCatalogoIptu tipo, UUID id) {
        if (catalogoIptuRepository.buscarPorId(tipo, id).isEmpty()) {
            throw new NotFoundException("Item de catálogo não encontrado.");
        }
        catalogoIptuRepository.excluir(tipo, id);
    }

    private static void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new ValidationException("Informe o nome do item de catálogo.");
        }
    }
}
