package br.com.tributos.iptu.adapters.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iptu.domain.ImovelTitularidadeHistorico;
import br.com.tributos.iptu.domain.ImovelTitularidadeHistoricoRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class ImovelTitularidadeHistoricoRepositoryAdapter implements ImovelTitularidadeHistoricoRepository {

    private final ImovelTitularidadeHistoricoJpaRepository jpaRepository;

    public ImovelTitularidadeHistoricoRepositoryAdapter(ImovelTitularidadeHistoricoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<ImovelTitularidadeHistorico> listarPorImovel(UUID imovelId) {
        return jpaRepository.findByImovelIdOrderByDataRegistroDesc(imovelId)
            .stream()
            .map(ImovelTitularidadeHistoricoRepositoryAdapter::paraDominio)
            .toList();
    }

    @Override
    public ImovelTitularidadeHistorico salvar(ImovelTitularidadeHistorico historico) {
        ImovelTitularidadeHistoricoJpaEntity entidade = new ImovelTitularidadeHistoricoJpaEntity();
        entidade.setId(historico.id());
        entidade.setTenantId(historico.tenantId() != null ? historico.tenantId() : TenantContext.getObrigatorio());
        entidade.setImovelId(historico.imovelId());
        entidade.setContribuinteId(historico.contribuinteId());
        entidade.setTipoRegistro(historico.tipoRegistro());
        entidade.setPorcentagem(historico.porcentagem());
        return paraDominio(jpaRepository.save(entidade));
    }

    private static ImovelTitularidadeHistorico paraDominio(ImovelTitularidadeHistoricoJpaEntity entidade) {
        return new ImovelTitularidadeHistorico(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getImovelId(),
            entidade.getContribuinteId(),
            entidade.getTipoRegistro(),
            entidade.getPorcentagem(),
            entidade.getDataRegistro()
        );
    }
}
