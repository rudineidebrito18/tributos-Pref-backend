package br.com.tributos.iptu.adapters.out.persistence;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iptu.domain.ImovelDestinacao;
import br.com.tributos.iptu.domain.ImovelDestinacaoRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class ImovelDestinacaoRepositoryAdapter implements ImovelDestinacaoRepository {

    private final ImovelDestinacaoJpaRepository jpaRepository;

    public ImovelDestinacaoRepositoryAdapter(ImovelDestinacaoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<ImovelDestinacao> listar() {
        return jpaRepository.findAll().stream().map(ImovelDestinacaoRepositoryAdapter::paraDominio).toList();
    }

    @Override
    public Optional<ImovelDestinacao> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(ImovelDestinacaoRepositoryAdapter::paraDominio);
    }

    @Override
    public ImovelDestinacao salvar(ImovelDestinacao destinacao) {
        UUID tenantId = TenantContext.getObrigatorio();
        ImovelDestinacaoJpaEntity entidade = jpaRepository.findById(destinacao.id())
            .orElseGet(ImovelDestinacaoJpaEntity::new);
        if (entidade.getId() == null) {
            entidade.setId(destinacao.id());
            entidade.setTenantId(tenantId);
        }
        entidade.setNome(destinacao.nome());
        entidade.setAtivo(destinacao.ativo());
        entidade.setTipoImovelId(destinacao.tipoImovelId());
        entidade.setAliquotaIptu(destinacao.aliquotaIptu() != null ? destinacao.aliquotaIptu() : BigDecimal.ZERO);
        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public void excluir(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existePorNome(String nome, UUID ignorarId) {
        if (ignorarId == null) {
            return jpaRepository.existsByNome(nome);
        }
        return jpaRepository.existsByNomeAndIdNot(nome, ignorarId);
    }

    private static ImovelDestinacao paraDominio(ImovelDestinacaoJpaEntity entidade) {
        return new ImovelDestinacao(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getNome(),
            entidade.isAtivo(),
            entidade.getTipoImovelId(),
            entidade.getAliquotaIptu(),
            entidade.getCriadoEm()
        );
    }
}
