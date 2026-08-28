package br.com.tributos.iss.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iss.domain.CatalogoIss;
import br.com.tributos.iss.domain.CatalogoIssRepository;
import br.com.tributos.iss.domain.TipoCatalogoIss;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class CatalogoIssRepositoryAdapter implements CatalogoIssRepository {

    private final TipoContribuinteJpaRepository tipoContribuinteJpaRepository;
    private final SituacaoCadastralJpaRepository situacaoCadastralJpaRepository;
    private final StatusCredenciamentoJpaRepository statusCredenciamentoJpaRepository;
    private final RegimeTributarioJpaRepository regimeTributarioJpaRepository;

    public CatalogoIssRepositoryAdapter(
        TipoContribuinteJpaRepository tipoContribuinteJpaRepository,
        SituacaoCadastralJpaRepository situacaoCadastralJpaRepository,
        StatusCredenciamentoJpaRepository statusCredenciamentoJpaRepository,
        RegimeTributarioJpaRepository regimeTributarioJpaRepository
    ) {
        this.tipoContribuinteJpaRepository = tipoContribuinteJpaRepository;
        this.situacaoCadastralJpaRepository = situacaoCadastralJpaRepository;
        this.statusCredenciamentoJpaRepository = statusCredenciamentoJpaRepository;
        this.regimeTributarioJpaRepository = regimeTributarioJpaRepository;
    }

    @Override
    public List<CatalogoIss> listar(TipoCatalogoIss tipo) {
        return switch (tipo) {
            case TIPO_CONTRIBUINTE -> tipoContribuinteJpaRepository.findAll().stream()
                .map(CatalogoIssRepositoryAdapter::paraDominio).toList();
            case SITUACAO_CADASTRAL -> situacaoCadastralJpaRepository.findAll().stream()
                .map(CatalogoIssRepositoryAdapter::paraDominio).toList();
            case STATUS_CREDENCIAMENTO -> statusCredenciamentoJpaRepository.findAll().stream()
                .map(CatalogoIssRepositoryAdapter::paraDominio).toList();
            case REGIME_TRIBUTARIO -> regimeTributarioJpaRepository.findAll().stream()
                .map(CatalogoIssRepositoryAdapter::paraDominio).toList();
        };
    }

    @Override
    public Optional<CatalogoIss> buscarPorId(TipoCatalogoIss tipo, UUID id) {
        return switch (tipo) {
            case TIPO_CONTRIBUINTE -> tipoContribuinteJpaRepository.findById(id).map(CatalogoIssRepositoryAdapter::paraDominio);
            case SITUACAO_CADASTRAL -> situacaoCadastralJpaRepository.findById(id).map(CatalogoIssRepositoryAdapter::paraDominio);
            case STATUS_CREDENCIAMENTO -> statusCredenciamentoJpaRepository.findById(id).map(CatalogoIssRepositoryAdapter::paraDominio);
            case REGIME_TRIBUTARIO -> regimeTributarioJpaRepository.findById(id).map(CatalogoIssRepositoryAdapter::paraDominio);
        };
    }

    @Override
    public Optional<CatalogoIss> buscarPorNome(TipoCatalogoIss tipo, String nome) {
        if (tipo != TipoCatalogoIss.STATUS_CREDENCIAMENTO) {
            return listar(tipo).stream()
                .filter(item -> item.nome().equalsIgnoreCase(nome))
                .findFirst();
        }
        return statusCredenciamentoJpaRepository.findByNome(nome).map(CatalogoIssRepositoryAdapter::paraDominio);
    }

    @Override
    public CatalogoIss salvar(TipoCatalogoIss tipo, CatalogoIss item) {
        UUID tenantId = TenantContext.getObrigatorio();
        return switch (tipo) {
            case TIPO_CONTRIBUINTE -> {
                TipoContribuinteJpaEntity entidade = tipoContribuinteJpaRepository.findById(item.id())
                    .orElseGet(TipoContribuinteJpaEntity::new);
                preencherCatalogo(entidade, item, tenantId);
                yield paraDominio(tipoContribuinteJpaRepository.save(entidade));
            }
            case SITUACAO_CADASTRAL -> {
                SituacaoCadastralJpaEntity entidade = situacaoCadastralJpaRepository.findById(item.id())
                    .orElseGet(SituacaoCadastralJpaEntity::new);
                preencherCatalogo(entidade, item, tenantId);
                yield paraDominio(situacaoCadastralJpaRepository.save(entidade));
            }
            case STATUS_CREDENCIAMENTO -> {
                StatusCredenciamentoJpaEntity entidade = statusCredenciamentoJpaRepository.findById(item.id())
                    .orElseGet(StatusCredenciamentoJpaEntity::new);
                preencherCatalogo(entidade, item, tenantId);
                yield paraDominio(statusCredenciamentoJpaRepository.save(entidade));
            }
            case REGIME_TRIBUTARIO -> {
                RegimeTributarioJpaEntity entidade = regimeTributarioJpaRepository.findById(item.id())
                    .orElseGet(RegimeTributarioJpaEntity::new);
                preencherCatalogo(entidade, item, tenantId);
                yield paraDominio(regimeTributarioJpaRepository.save(entidade));
            }
        };
    }

    @Override
    public void excluir(TipoCatalogoIss tipo, UUID id) {
        switch (tipo) {
            case TIPO_CONTRIBUINTE -> tipoContribuinteJpaRepository.deleteById(id);
            case SITUACAO_CADASTRAL -> situacaoCadastralJpaRepository.deleteById(id);
            case STATUS_CREDENCIAMENTO -> statusCredenciamentoJpaRepository.deleteById(id);
            case REGIME_TRIBUTARIO -> regimeTributarioJpaRepository.deleteById(id);
        }
    }

    @Override
    public boolean existePorNome(TipoCatalogoIss tipo, String nome, UUID ignorarId) {
        if (ignorarId == null) {
            return switch (tipo) {
                case TIPO_CONTRIBUINTE -> tipoContribuinteJpaRepository.existsByNome(nome);
                case SITUACAO_CADASTRAL -> situacaoCadastralJpaRepository.existsByNome(nome);
                case STATUS_CREDENCIAMENTO -> statusCredenciamentoJpaRepository.existsByNome(nome);
                case REGIME_TRIBUTARIO -> regimeTributarioJpaRepository.existsByNome(nome);
            };
        }
        return switch (tipo) {
            case TIPO_CONTRIBUINTE -> tipoContribuinteJpaRepository.existsByNomeAndIdNot(nome, ignorarId);
            case SITUACAO_CADASTRAL -> situacaoCadastralJpaRepository.existsByNomeAndIdNot(nome, ignorarId);
            case STATUS_CREDENCIAMENTO -> statusCredenciamentoJpaRepository.existsByNomeAndIdNot(nome, ignorarId);
            case REGIME_TRIBUTARIO -> regimeTributarioJpaRepository.existsByNomeAndIdNot(nome, ignorarId);
        };
    }

    private static void preencherCatalogo(CatalogoIssJpaEntityBase entidade, CatalogoIss item, UUID tenantId) {
        if (entidade.getId() == null) {
            entidade.setId(item.id());
            entidade.setTenantId(tenantId);
        }
        entidade.setNome(item.nome());
        entidade.setAtivo(item.ativo());
    }

    private static CatalogoIss paraDominio(CatalogoIssJpaEntityBase entidade) {
        return new CatalogoIss(entidade.getId(), entidade.getTenantId(), entidade.getNome(), entidade.isAtivo());
    }
}
