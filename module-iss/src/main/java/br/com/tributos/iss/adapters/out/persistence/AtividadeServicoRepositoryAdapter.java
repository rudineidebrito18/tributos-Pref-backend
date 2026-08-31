package br.com.tributos.iss.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import br.com.tributos.iss.domain.AtividadeServico;
import br.com.tributos.iss.domain.AtividadeServicoRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class AtividadeServicoRepositoryAdapter implements AtividadeServicoRepository {

    private final AtividadeServicoJpaRepository jpaRepository;

    public AtividadeServicoRepositoryAdapter(AtividadeServicoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public AtividadeServico salvar(AtividadeServico atividadeServico) {
        UUID tenantId = TenantContext.getObrigatorio();
        AtividadeServicoJpaEntity entidade = jpaRepository.findById(atividadeServico.id())
            .orElseGet(() -> {
                AtividadeServicoJpaEntity nova = new AtividadeServicoJpaEntity();
                nova.setId(atividadeServico.id());
                nova.setTenantId(tenantId);
                return nova;
            });

        entidade.setAtividadeId(atividadeServico.atividadeId());
        entidade.setServicoId(atividadeServico.servicoId());
        entidade.setLocalIncidenciaId(atividadeServico.localIncidenciaId());
        entidade.setAliquota(atividadeServico.aliquota());
        entidade.setTributavel(atividadeServico.tributavel());
        entidade.setImune(atividadeServico.imune());
        entidade.setDeducao(atividadeServico.deducao());
        entidade.setSubstitutoTributario(atividadeServico.substitutoTributario());
        entidade.setRetencaoFonte(atividadeServico.retencaoFonte());
        entidade.setRegimeEspecial(atividadeServico.regimeEspecial());
        entidade.setObservacao(atividadeServico.observacao());

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public Optional<AtividadeServico> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(AtividadeServicoRepositoryAdapter::paraDominio);
    }

    @Override
    public Optional<AtividadeServico> buscarPorAtividadeEServico(UUID atividadeId, UUID servicoId) {
        return jpaRepository.findByAtividadeIdAndServicoId(atividadeId, servicoId)
            .map(AtividadeServicoRepositoryAdapter::paraDominio);
    }

    @Override
    public boolean existePorAtividadeEServico(UUID atividadeId, UUID servicoId, UUID ignorarId) {
        if (ignorarId == null) {
            return jpaRepository.existsByAtividadeIdAndServicoId(atividadeId, servicoId);
        }
        return jpaRepository.existsByAtividadeIdAndServicoIdAndIdNot(atividadeId, servicoId, ignorarId);
    }

    @Override
    public Page<AtividadeServico> listar(String codigoCnae, String codigoServico, Pageable pageable) {
        return jpaRepository.listarComFiltro(codigoCnae, codigoServico, pageable)
            .map(AtividadeServicoRepositoryAdapter::paraDominio);
    }

    @Override
    public Page<AtividadeServicoView> listarView(String codigoCnae, String codigoServico, Pageable pageable) {
        return jpaRepository.listarView(codigoCnae, codigoServico, pageable)
            .map(p -> new AtividadeServicoView(
                p.getId(),
                p.getCnae(),
                p.getCodigo(),
                p.getServico(),
                p.getAliquota(),
                p.getTributavel(),
                p.getDeducao(),
                p.getRetencao(),
                p.getIncidencia()
            ));
    }

    private static AtividadeServico paraDominio(AtividadeServicoJpaEntity entidade) {
        return new AtividadeServico(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getAtividadeId(),
            entidade.getServicoId(),
            entidade.getLocalIncidenciaId(),
            entidade.getAliquota(),
            entidade.isTributavel(),
            entidade.isImune(),
            entidade.isDeducao(),
            entidade.isSubstitutoTributario(),
            entidade.isRetencaoFonte(),
            entidade.getRegimeEspecial(),
            entidade.getObservacao()
        );
    }
}
