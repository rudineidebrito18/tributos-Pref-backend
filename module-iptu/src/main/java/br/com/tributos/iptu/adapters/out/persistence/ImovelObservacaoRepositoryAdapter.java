package br.com.tributos.iptu.adapters.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iptu.domain.ImovelObservacao;
import br.com.tributos.iptu.domain.ImovelObservacaoRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class ImovelObservacaoRepositoryAdapter implements ImovelObservacaoRepository {

    private final ImovelObservacaoJpaRepository jpaRepository;

    public ImovelObservacaoRepositoryAdapter(ImovelObservacaoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ImovelObservacao salvar(ImovelObservacao observacao) {
        UUID tenantId = TenantContext.getObrigatorio();
        ImovelObservacaoJpaEntity entidade = new ImovelObservacaoJpaEntity();
        entidade.setId(observacao.id());
        entidade.setTenantId(tenantId);
        entidade.setImovelId(observacao.imovelId());
        entidade.setUsuarioId(observacao.usuarioId());
        entidade.setTexto(observacao.texto());

        ImovelObservacaoJpaEntity salva = jpaRepository.save(entidade);
        return paraDominio(salva);
    }

    @Override
    public List<ImovelObservacao> listarPorImovel(UUID imovelId) {
        return jpaRepository.findByImovelIdOrderByCriadoEmDesc(imovelId)
            .stream()
            .map(ImovelObservacaoRepositoryAdapter::paraDominio)
            .toList();
    }

    private static ImovelObservacao paraDominio(ImovelObservacaoJpaEntity entidade) {
        return new ImovelObservacao(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getImovelId(),
            entidade.getUsuarioId(),
            entidade.getTexto(),
            entidade.getCriadoEm()
        );
    }
}
