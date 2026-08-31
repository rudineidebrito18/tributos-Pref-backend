package br.com.tributos.iss.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iss.domain.GrupoServico;
import br.com.tributos.iss.domain.GrupoServicoRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class GrupoServicoRepositoryAdapter implements GrupoServicoRepository {

    private final GrupoServicoJpaRepository jpaRepository;

    public GrupoServicoRepositoryAdapter(GrupoServicoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<GrupoServico> listar() {
        return jpaRepository.findAll().stream().map(GrupoServicoRepositoryAdapter::paraDominio).toList();
    }

    @Override
    public Optional<GrupoServico> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(GrupoServicoRepositoryAdapter::paraDominio);
    }

    @Override
    public GrupoServico salvar(GrupoServico grupo) {
        UUID tenantId = TenantContext.getObrigatorio();
        GrupoServicoJpaEntity entidade = jpaRepository.findById(grupo.id())
            .orElseGet(() -> {
                GrupoServicoJpaEntity nova = new GrupoServicoJpaEntity();
                nova.setId(grupo.id());
                nova.setTenantId(tenantId);
                return nova;
            });

        entidade.setCodigo(grupo.codigo());
        entidade.setDescricao(grupo.descricao());

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public void excluir(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existePorCodigo(String codigo, UUID excetoId) {
        if (excetoId == null) {
            return jpaRepository.existsByCodigo(codigo);
        }
        return jpaRepository.existsByCodigoAndIdNot(codigo, excetoId);
    }

    private static GrupoServico paraDominio(GrupoServicoJpaEntity entidade) {
        return new GrupoServico(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getCodigo(),
            entidade.getDescricao()
        );
    }
}
