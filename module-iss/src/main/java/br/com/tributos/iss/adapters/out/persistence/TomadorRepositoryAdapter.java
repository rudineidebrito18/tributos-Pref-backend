package br.com.tributos.iss.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import br.com.tributos.iss.domain.Tomador;
import br.com.tributos.iss.domain.TomadorRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class TomadorRepositoryAdapter implements TomadorRepository {

    private final TomadorJpaRepository jpaRepository;

    public TomadorRepositoryAdapter(TomadorJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Tomador salvar(Tomador tomador) {
        UUID tenantId = TenantContext.getObrigatorio();
        TomadorJpaEntity entidade = jpaRepository.findById(tomador.id())
            .orElseGet(() -> {
                TomadorJpaEntity nova = new TomadorJpaEntity();
                nova.setId(tomador.id());
                nova.setTenantId(tenantId);
                return nova;
            });

        entidade.setPessoaId(tomador.pessoaId());
        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public Optional<Tomador> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(TomadorRepositoryAdapter::paraDominio);
    }

    @Override
    public Page<Tomador> listar(Pageable pageable) {
        return jpaRepository.findAllByOrderByCriadoEmDesc(pageable).map(TomadorRepositoryAdapter::paraDominio);
    }

    @Override
    public boolean existePorPessoaId(UUID pessoaId, UUID ignorarId) {
        if (ignorarId == null) {
            return jpaRepository.existsByPessoaId(pessoaId);
        }
        return jpaRepository.existsByPessoaIdAndIdNot(pessoaId, ignorarId);
    }

    private static Tomador paraDominio(TomadorJpaEntity entidade) {
        return new Tomador(entidade.getId(), entidade.getTenantId(), entidade.getPessoaId());
    }
}
