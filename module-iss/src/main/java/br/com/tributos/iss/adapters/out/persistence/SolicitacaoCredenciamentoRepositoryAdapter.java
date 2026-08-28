package br.com.tributos.iss.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import br.com.tributos.iss.domain.SolicitacaoCredenciamento;
import br.com.tributos.iss.domain.SolicitacaoCredenciamentoRepository;
import br.com.tributos.iss.domain.StatusCredenciamentoNomes;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class SolicitacaoCredenciamentoRepositoryAdapter implements SolicitacaoCredenciamentoRepository {

    private final SolicitacaoCredenciamentoJpaRepository jpaRepository;
    private final StatusCredenciamentoJpaRepository statusCredenciamentoJpaRepository;

    public SolicitacaoCredenciamentoRepositoryAdapter(
        SolicitacaoCredenciamentoJpaRepository jpaRepository,
        StatusCredenciamentoJpaRepository statusCredenciamentoJpaRepository
    ) {
        this.jpaRepository = jpaRepository;
        this.statusCredenciamentoJpaRepository = statusCredenciamentoJpaRepository;
    }

    @Override
    public SolicitacaoCredenciamento salvar(SolicitacaoCredenciamento solicitacao) {
        UUID tenantId = TenantContext.getObrigatorio();
        SolicitacaoCredenciamentoJpaEntity entidade = jpaRepository.findById(solicitacao.id())
            .orElseGet(() -> {
                SolicitacaoCredenciamentoJpaEntity nova = new SolicitacaoCredenciamentoJpaEntity();
                nova.setId(solicitacao.id());
                nova.setTenantId(tenantId);
                return nova;
            });

        entidade.setContribuinteId(solicitacao.contribuinteId());
        entidade.setStatusId(solicitacao.statusId());
        entidade.setObservacao(solicitacao.observacao());
        entidade.setAnalisadoPor(solicitacao.analisadoPor());
        entidade.setAnalisadoEm(solicitacao.analisadoEm());

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public Optional<SolicitacaoCredenciamento> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(SolicitacaoCredenciamentoRepositoryAdapter::paraDominio);
    }

    @Override
    public Page<SolicitacaoCredenciamento> listar(Pageable pageable) {
        return jpaRepository.findAllByOrderByCriadoEmDesc(pageable)
            .map(SolicitacaoCredenciamentoRepositoryAdapter::paraDominio);
    }

    @Override
    public Optional<SolicitacaoCredenciamento> buscarEmAnalisePorContribuinte(UUID contribuinteId) {
        UUID statusEmAnaliseId = statusCredenciamentoJpaRepository.findByNome(StatusCredenciamentoNomes.EM_ANALISE)
            .map(StatusCredenciamentoJpaEntity::getId)
            .orElseThrow(() -> new IllegalStateException(
                "Status de credenciamento EM_ANALISE não encontrado no catálogo do tenant."));
        return jpaRepository.findEmAnalisePorContribuinte(contribuinteId, statusEmAnaliseId)
            .map(SolicitacaoCredenciamentoRepositoryAdapter::paraDominio);
    }

    private static SolicitacaoCredenciamento paraDominio(SolicitacaoCredenciamentoJpaEntity entidade) {
        return new SolicitacaoCredenciamento(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getContribuinteId(),
            entidade.getStatusId(),
            entidade.getObservacao(),
            entidade.getAnalisadoPor(),
            entidade.getAnalisadoEm(),
            entidade.getCriadoEm()
        );
    }
}
