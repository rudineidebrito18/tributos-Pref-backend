package br.com.tributos.cadastro.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.cadastro.domain.DocumentoCategoria;
import br.com.tributos.cadastro.domain.DocumentoCategoriaRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class DocumentoCategoriaRepositoryAdapter implements DocumentoCategoriaRepository {

    private final DocumentoCategoriaJpaRepository jpaRepository;

    public DocumentoCategoriaRepositoryAdapter(DocumentoCategoriaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public DocumentoCategoria salvar(DocumentoCategoria categoria) {
        DocumentoCategoriaJpaEntity entidade = jpaRepository.findById(categoria.id())
            .orElseGet(() -> {
                DocumentoCategoriaJpaEntity nova = new DocumentoCategoriaJpaEntity(
                    categoria.id(), categoria.tenantId(), categoria.nome()
                );
                nova.setId(categoria.id());
                nova.setTenantId(categoria.tenantId());
                return nova;
            });
        entidade.setNome(categoria.nome());
        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public Optional<DocumentoCategoria> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(DocumentoCategoriaRepositoryAdapter::paraDominio);
    }

    @Override
    public List<DocumentoCategoria> listar() {
        UUID tenantId = TenantContext.getObrigatorio();
        return jpaRepository.findByTenantIdOrderByNomeAsc(tenantId).stream()
            .map(DocumentoCategoriaRepositoryAdapter::paraDominio)
            .toList();
    }

    @Override
    public boolean existePorNome(String nome, UUID excluirId) {
        UUID tenantId = TenantContext.getObrigatorio();
        return jpaRepository.existsPorNome(tenantId, nome, excluirId);
    }

    @Override
    public void excluir(UUID id) {
        jpaRepository.deleteById(id);
    }

    private static DocumentoCategoria paraDominio(DocumentoCategoriaJpaEntity entidade) {
        return new DocumentoCategoria(
            entidade.getId(), entidade.getTenantId(), entidade.getNome(), entidade.getCriadoEm()
        );
    }
}
