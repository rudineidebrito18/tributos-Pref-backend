package br.com.tributos.iss.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iss.domain.Atividade;
import br.com.tributos.iss.domain.AtividadeRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class AtividadeRepositoryAdapter implements AtividadeRepository {

    private final AtividadeJpaRepository jpaRepository;

    public AtividadeRepositoryAdapter(AtividadeJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Atividade> listar() {
        return jpaRepository.findAll().stream().map(AtividadeRepositoryAdapter::paraDominio).toList();
    }

    @Override
    public Optional<Atividade> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(AtividadeRepositoryAdapter::paraDominio);
    }

    @Override
    public Atividade salvar(Atividade atividade) {
        UUID tenantId = TenantContext.getObrigatorio();
        AtividadeJpaEntity entidade = jpaRepository.findById(atividade.id())
            .orElseGet(() -> {
                AtividadeJpaEntity nova = new AtividadeJpaEntity();
                nova.setId(atividade.id());
                nova.setTenantId(tenantId);
                return nova;
            });

        entidade.setCodigo(atividade.codigo());
        entidade.setDescricao(atividade.descricao());
        entidade.setAtivo(atividade.ativo());

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public void excluir(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existePorCodigo(String codigo, UUID ignorarId) {
        if (ignorarId == null) {
            return jpaRepository.existsByCodigo(codigo);
        }
        return jpaRepository.existsByCodigoAndIdNot(codigo, ignorarId);
    }

    private static Atividade paraDominio(AtividadeJpaEntity entidade) {
        return new Atividade(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getCodigo(),
            entidade.getDescricao(),
            entidade.isAtivo()
        );
    }
}
