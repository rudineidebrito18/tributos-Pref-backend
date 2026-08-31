package br.com.tributos.iss.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iss.domain.StatusSolicitacao;
import br.com.tributos.iss.domain.StatusSolicitacaoRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class StatusSolicitacaoRepositoryAdapter implements StatusSolicitacaoRepository {

    private final StatusSolicitacaoJpaRepository jpaRepository;

    public StatusSolicitacaoRepositoryAdapter(StatusSolicitacaoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<StatusSolicitacao> listar() {
        return jpaRepository.findAll().stream().map(StatusSolicitacaoRepositoryAdapter::paraDominio).toList();
    }

    @Override
    public Optional<StatusSolicitacao> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(StatusSolicitacaoRepositoryAdapter::paraDominio);
    }

    @Override
    public StatusSolicitacao salvar(StatusSolicitacao status) {
        UUID tenantId = TenantContext.getObrigatorio();
        StatusSolicitacaoJpaEntity entidade = jpaRepository.findById(status.id())
            .orElseGet(() -> {
                StatusSolicitacaoJpaEntity nova = new StatusSolicitacaoJpaEntity();
                nova.setId(status.id());
                nova.setTenantId(tenantId);
                return nova;
            });

        entidade.setDescricao(status.descricao());
        entidade.setAtivo(status.ativo());

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public void excluir(UUID id) {
        jpaRepository.deleteById(id);
    }

    private static StatusSolicitacao paraDominio(StatusSolicitacaoJpaEntity entidade) {
        return new StatusSolicitacao(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getDescricao(),
            entidade.isAtivo()
        );
    }
}
