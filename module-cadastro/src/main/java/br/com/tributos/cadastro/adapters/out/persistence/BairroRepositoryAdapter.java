package br.com.tributos.cadastro.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.cadastro.domain.Bairro;
import br.com.tributos.cadastro.domain.BairroRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class BairroRepositoryAdapter implements BairroRepository {

    private final BairroJpaRepository jpaRepository;

    public BairroRepositoryAdapter(BairroJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Bairro> listar(UUID cidadeId) {
        return jpaRepository.findByCidadeIdOrderByNome(cidadeId).stream().map(BairroRepositoryAdapter::paraDominio).toList();
    }

    @Override
    public Optional<Bairro> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(BairroRepositoryAdapter::paraDominio);
    }

    @Override
    public Bairro salvar(Bairro bairro) {
        UUID tenantId = TenantContext.getObrigatorio();
        BairroJpaEntity entidade = jpaRepository.findById(bairro.id()).orElseGet(BairroJpaEntity::new);
        if (entidade.getId() == null) {
            entidade.setId(bairro.id());
            entidade.setTenantId(tenantId);
        }
        entidade.setCidadeId(bairro.cidadeId());
        entidade.setNome(bairro.nome());
        entidade.setZonaFiscalId(bairro.zonaFiscalId());
        entidade.setValorTerreno(bairro.valorTerreno());
        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public void excluir(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existePorNome(UUID cidadeId, String nome, UUID ignorarId) {
        if (ignorarId == null) {
            return jpaRepository.existsByCidadeIdAndNome(cidadeId, nome);
        }
        return jpaRepository.existsByCidadeIdAndNomeAndIdNot(cidadeId, nome, ignorarId);
    }

    private static Bairro paraDominio(BairroJpaEntity entidade) {
        return new Bairro(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getCidadeId(),
            entidade.getNome(),
            entidade.getZonaFiscalId(),
            entidade.getValorTerreno(),
            entidade.getCriadoEm()
        );
    }
}
