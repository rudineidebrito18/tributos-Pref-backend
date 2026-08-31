package br.com.tributos.itbi.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.itbi.domain.TipoCalculoGuiaItbi;
import br.com.tributos.itbi.domain.TipoCalculoGuiaItbiRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class TipoCalculoGuiaItbiRepositoryAdapter implements TipoCalculoGuiaItbiRepository {

    private final TipoCalculoGuiaItbiJpaRepository jpaRepository;

    public TipoCalculoGuiaItbiRepositoryAdapter(TipoCalculoGuiaItbiJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<TipoCalculoGuiaItbi> listar() {
        return jpaRepository.findAll().stream().map(TipoCalculoGuiaItbiRepositoryAdapter::paraDominio).toList();
    }

    @Override
    public Optional<TipoCalculoGuiaItbi> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(TipoCalculoGuiaItbiRepositoryAdapter::paraDominio);
    }

    @Override
    public TipoCalculoGuiaItbi salvar(TipoCalculoGuiaItbi tipoCalculo) {
        UUID tenantId = TenantContext.getObrigatorio();
        TipoCalculoGuiaItbiJpaEntity entidade = jpaRepository.findById(tipoCalculo.id())
            .orElseGet(TipoCalculoGuiaItbiJpaEntity::new);
        if (entidade.getId() == null) {
            entidade.setId(tipoCalculo.id());
            entidade.setTenantId(tenantId);
        }
        entidade.setDescricao(tipoCalculo.descricao());
        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public void excluir(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existePorDescricao(String descricao, UUID ignorarId) {
        if (ignorarId == null) {
            return jpaRepository.existsByDescricao(descricao);
        }
        return jpaRepository.existsByDescricaoAndIdNot(descricao, ignorarId);
    }

    private static TipoCalculoGuiaItbi paraDominio(TipoCalculoGuiaItbiJpaEntity entidade) {
        return new TipoCalculoGuiaItbi(entidade.getId(), entidade.getTenantId(), entidade.getDescricao());
    }
}
