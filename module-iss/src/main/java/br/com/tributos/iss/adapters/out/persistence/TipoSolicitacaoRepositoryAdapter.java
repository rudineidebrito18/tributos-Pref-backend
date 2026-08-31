package br.com.tributos.iss.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iss.domain.TipoSolicitacao;
import br.com.tributos.iss.domain.TipoSolicitacaoRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class TipoSolicitacaoRepositoryAdapter implements TipoSolicitacaoRepository {

    private final TipoSolicitacaoJpaRepository jpaRepository;

    public TipoSolicitacaoRepositoryAdapter(TipoSolicitacaoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<TipoSolicitacao> listar() {
        return jpaRepository.findAll().stream().map(TipoSolicitacaoRepositoryAdapter::paraDominio).toList();
    }

    @Override
    public Optional<TipoSolicitacao> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(TipoSolicitacaoRepositoryAdapter::paraDominio);
    }

    @Override
    public TipoSolicitacao salvar(TipoSolicitacao tipo) {
        UUID tenantId = TenantContext.getObrigatorio();
        TipoSolicitacaoJpaEntity entidade = jpaRepository.findById(tipo.id())
            .orElseGet(() -> {
                TipoSolicitacaoJpaEntity nova = new TipoSolicitacaoJpaEntity();
                nova.setId(tipo.id());
                nova.setTenantId(tenantId);
                return nova;
            });

        entidade.setDescricao(tipo.descricao());
        entidade.setUsuarioNotificarId(tipo.usuarioNotificarId());
        entidade.setAtivo(tipo.ativo());

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public void excluir(UUID id) {
        jpaRepository.deleteById(id);
    }

    private static TipoSolicitacao paraDominio(TipoSolicitacaoJpaEntity entidade) {
        return new TipoSolicitacao(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getDescricao(),
            entidade.getUsuarioNotificarId(),
            entidade.isAtivo()
        );
    }
}
