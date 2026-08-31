package br.com.tributos.iss.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import br.com.tributos.iss.domain.Solicitacao;
import br.com.tributos.iss.domain.SolicitacaoRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class SolicitacaoRepositoryAdapter implements SolicitacaoRepository {

    private final SolicitacaoJpaRepository jpaRepository;

    public SolicitacaoRepositoryAdapter(SolicitacaoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Solicitacao salvar(Solicitacao solicitacao) {
        UUID tenantId = TenantContext.getObrigatorio();
        SolicitacaoJpaEntity entidade = jpaRepository.findById(solicitacao.id())
            .orElseGet(() -> {
                SolicitacaoJpaEntity nova = new SolicitacaoJpaEntity();
                nova.setId(solicitacao.id());
                nova.setTenantId(tenantId);
                return nova;
            });

        entidade.setUsuarioId(solicitacao.usuarioId());
        entidade.setTipoSolicitacaoId(solicitacao.tipoSolicitacaoId());
        entidade.setStatusSolicitacaoId(solicitacao.statusSolicitacaoId());
        entidade.setDescricao(solicitacao.descricao());
        entidade.setDataHora(solicitacao.dataHora());

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public Optional<Solicitacao> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(SolicitacaoRepositoryAdapter::paraDominio);
    }

    @Override
    public Page<Solicitacao> listar(UUID tipoSolicitacaoId, UUID statusSolicitacaoId, Pageable pageable) {
        return jpaRepository.buscarComFiltro(tipoSolicitacaoId, statusSolicitacaoId, pageable)
            .map(SolicitacaoRepositoryAdapter::paraDominio);
    }

    private static Solicitacao paraDominio(SolicitacaoJpaEntity entidade) {
        return new Solicitacao(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getUsuarioId(),
            entidade.getTipoSolicitacaoId(),
            entidade.getStatusSolicitacaoId(),
            entidade.getDescricao(),
            entidade.getDataHora(),
            entidade.getCriadoEm()
        );
    }
}
