package br.com.tributos.iss.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iss.domain.LocalIncidencia;
import br.com.tributos.iss.domain.LocalIncidenciaRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class LocalIncidenciaRepositoryAdapter implements LocalIncidenciaRepository {

    private final LocalIncidenciaJpaRepository jpaRepository;

    public LocalIncidenciaRepositoryAdapter(LocalIncidenciaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<LocalIncidencia> listar() {
        return jpaRepository.findAll().stream().map(LocalIncidenciaRepositoryAdapter::paraDominio).toList();
    }

    @Override
    public Optional<LocalIncidencia> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(LocalIncidenciaRepositoryAdapter::paraDominio);
    }

    @Override
    public LocalIncidencia salvar(LocalIncidencia local) {
        UUID tenantId = TenantContext.getObrigatorio();
        LocalIncidenciaJpaEntity entidade = jpaRepository.findById(local.id())
            .orElseGet(() -> {
                LocalIncidenciaJpaEntity nova = new LocalIncidenciaJpaEntity();
                nova.setId(local.id());
                nova.setTenantId(tenantId);
                return nova;
            });

        entidade.setDescricao(local.descricao());
        entidade.setAtivo(local.ativo());

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

    private static LocalIncidencia paraDominio(LocalIncidenciaJpaEntity entidade) {
        return new LocalIncidencia(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getDescricao(),
            entidade.isAtivo()
        );
    }
}
