package br.com.tributos.iss.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iss.domain.SituacaoCnd;
import br.com.tributos.iss.domain.SituacaoCndRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class SituacaoCndRepositoryAdapter implements SituacaoCndRepository {

    private final SituacaoCndJpaRepository jpaRepository;

    public SituacaoCndRepositoryAdapter(SituacaoCndJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<SituacaoCnd> listar() {
        return jpaRepository.findAll().stream().map(SituacaoCndRepositoryAdapter::paraDominio).toList();
    }

    @Override
    public Optional<SituacaoCnd> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(SituacaoCndRepositoryAdapter::paraDominio);
    }

    @Override
    public SituacaoCnd salvar(SituacaoCnd situacao) {
        UUID tenantId = TenantContext.getObrigatorio();
        SituacaoCndJpaEntity entidade = jpaRepository.findById(situacao.id())
            .orElseGet(() -> {
                SituacaoCndJpaEntity nova = new SituacaoCndJpaEntity();
                nova.setId(situacao.id());
                nova.setTenantId(tenantId);
                return nova;
            });

        entidade.setDescricao(situacao.descricao());
        entidade.setTitulo(situacao.titulo());
        entidade.setAtivo(situacao.ativo());

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public void excluir(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existePorDescricao(String descricao, UUID excetoId) {
        if (excetoId == null) {
            return jpaRepository.existsByDescricao(descricao);
        }
        return jpaRepository.existsByDescricaoAndIdNot(descricao, excetoId);
    }

    private static SituacaoCnd paraDominio(SituacaoCndJpaEntity entidade) {
        return new SituacaoCnd(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getDescricao(),
            entidade.getTitulo(),
            entidade.isAtivo()
        );
    }
}
