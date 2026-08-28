package br.com.tributos.iptu.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iptu.domain.CatalogoIptu;
import br.com.tributos.iptu.domain.CatalogoIptuRepository;
import br.com.tributos.iptu.domain.TipoCatalogoIptu;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class CatalogoIptuRepositoryAdapter implements CatalogoIptuRepository {

    private final ImovelTipoJpaRepository imovelTipoJpaRepository;
    private final ImovelTipoEdificacaoJpaRepository imovelTipoEdificacaoJpaRepository;
    private final ImovelDestinacaoJpaRepository imovelDestinacaoJpaRepository;
    private final ImovelTipoLimitacaoJpaRepository imovelTipoLimitacaoJpaRepository;
    private final ImovelHabiteseTipoJpaRepository imovelHabiteseTipoJpaRepository;

    public CatalogoIptuRepositoryAdapter(
        ImovelTipoJpaRepository imovelTipoJpaRepository,
        ImovelTipoEdificacaoJpaRepository imovelTipoEdificacaoJpaRepository,
        ImovelDestinacaoJpaRepository imovelDestinacaoJpaRepository,
        ImovelTipoLimitacaoJpaRepository imovelTipoLimitacaoJpaRepository,
        ImovelHabiteseTipoJpaRepository imovelHabiteseTipoJpaRepository
    ) {
        this.imovelTipoJpaRepository = imovelTipoJpaRepository;
        this.imovelTipoEdificacaoJpaRepository = imovelTipoEdificacaoJpaRepository;
        this.imovelDestinacaoJpaRepository = imovelDestinacaoJpaRepository;
        this.imovelTipoLimitacaoJpaRepository = imovelTipoLimitacaoJpaRepository;
        this.imovelHabiteseTipoJpaRepository = imovelHabiteseTipoJpaRepository;
    }

    @Override
    public List<CatalogoIptu> listar(TipoCatalogoIptu tipo) {
        return switch (tipo) {
            case TIPO_IMOVEL -> imovelTipoJpaRepository.findAll().stream()
                .map(CatalogoIptuRepositoryAdapter::paraDominio).toList();
            case TIPO_EDIFICACAO -> imovelTipoEdificacaoJpaRepository.findAll().stream()
                .map(CatalogoIptuRepositoryAdapter::paraDominio).toList();
            case DESTINACAO -> imovelDestinacaoJpaRepository.findAll().stream()
                .map(CatalogoIptuRepositoryAdapter::paraDominio).toList();
            case TIPO_LIMITACAO -> imovelTipoLimitacaoJpaRepository.findAll().stream()
                .map(CatalogoIptuRepositoryAdapter::paraDominio).toList();
            case HABITESE_TIPO -> imovelHabiteseTipoJpaRepository.findAll().stream()
                .map(CatalogoIptuRepositoryAdapter::paraDominio).toList();
        };
    }

    @Override
    public Optional<CatalogoIptu> buscarPorId(TipoCatalogoIptu tipo, UUID id) {
        return switch (tipo) {
            case TIPO_IMOVEL -> imovelTipoJpaRepository.findById(id).map(CatalogoIptuRepositoryAdapter::paraDominio);
            case TIPO_EDIFICACAO -> imovelTipoEdificacaoJpaRepository.findById(id).map(CatalogoIptuRepositoryAdapter::paraDominio);
            case DESTINACAO -> imovelDestinacaoJpaRepository.findById(id).map(CatalogoIptuRepositoryAdapter::paraDominio);
            case TIPO_LIMITACAO -> imovelTipoLimitacaoJpaRepository.findById(id).map(CatalogoIptuRepositoryAdapter::paraDominio);
            case HABITESE_TIPO -> imovelHabiteseTipoJpaRepository.findById(id).map(CatalogoIptuRepositoryAdapter::paraDominio);
        };
    }

    @Override
    public Optional<CatalogoIptu> buscarPorNome(TipoCatalogoIptu tipo, String nome) {
        return listar(tipo).stream()
            .filter(item -> item.nome().equalsIgnoreCase(nome))
            .findFirst();
    }

    @Override
    public CatalogoIptu salvar(TipoCatalogoIptu tipo, CatalogoIptu item) {
        UUID tenantId = TenantContext.getObrigatorio();
        return switch (tipo) {
            case TIPO_IMOVEL -> {
                ImovelTipoJpaEntity entidade = imovelTipoJpaRepository.findById(item.id())
                    .orElseGet(ImovelTipoJpaEntity::new);
                preencherCatalogo(entidade, item, tenantId);
                yield paraDominio(imovelTipoJpaRepository.save(entidade));
            }
            case TIPO_EDIFICACAO -> {
                ImovelTipoEdificacaoJpaEntity entidade = imovelTipoEdificacaoJpaRepository.findById(item.id())
                    .orElseGet(ImovelTipoEdificacaoJpaEntity::new);
                preencherCatalogo(entidade, item, tenantId);
                yield paraDominio(imovelTipoEdificacaoJpaRepository.save(entidade));
            }
            case DESTINACAO -> {
                ImovelDestinacaoJpaEntity entidade = imovelDestinacaoJpaRepository.findById(item.id())
                    .orElseGet(ImovelDestinacaoJpaEntity::new);
                preencherCatalogo(entidade, item, tenantId);
                yield paraDominio(imovelDestinacaoJpaRepository.save(entidade));
            }
            case TIPO_LIMITACAO -> {
                ImovelTipoLimitacaoJpaEntity entidade = imovelTipoLimitacaoJpaRepository.findById(item.id())
                    .orElseGet(ImovelTipoLimitacaoJpaEntity::new);
                preencherCatalogo(entidade, item, tenantId);
                yield paraDominio(imovelTipoLimitacaoJpaRepository.save(entidade));
            }
            case HABITESE_TIPO -> {
                ImovelHabiteseTipoJpaEntity entidade = imovelHabiteseTipoJpaRepository.findById(item.id())
                    .orElseGet(ImovelHabiteseTipoJpaEntity::new);
                preencherCatalogo(entidade, item, tenantId);
                yield paraDominio(imovelHabiteseTipoJpaRepository.save(entidade));
            }
        };
    }

    @Override
    public void excluir(TipoCatalogoIptu tipo, UUID id) {
        switch (tipo) {
            case TIPO_IMOVEL -> imovelTipoJpaRepository.deleteById(id);
            case TIPO_EDIFICACAO -> imovelTipoEdificacaoJpaRepository.deleteById(id);
            case DESTINACAO -> imovelDestinacaoJpaRepository.deleteById(id);
            case TIPO_LIMITACAO -> imovelTipoLimitacaoJpaRepository.deleteById(id);
            case HABITESE_TIPO -> imovelHabiteseTipoJpaRepository.deleteById(id);
        }
    }

    @Override
    public boolean existePorNome(TipoCatalogoIptu tipo, String nome, UUID ignorarId) {
        if (ignorarId == null) {
            return switch (tipo) {
                case TIPO_IMOVEL -> imovelTipoJpaRepository.existsByNome(nome);
                case TIPO_EDIFICACAO -> imovelTipoEdificacaoJpaRepository.existsByNome(nome);
                case DESTINACAO -> imovelDestinacaoJpaRepository.existsByNome(nome);
                case TIPO_LIMITACAO -> imovelTipoLimitacaoJpaRepository.existsByNome(nome);
                case HABITESE_TIPO -> imovelHabiteseTipoJpaRepository.existsByNome(nome);
            };
        }
        return switch (tipo) {
            case TIPO_IMOVEL -> imovelTipoJpaRepository.existsByNomeAndIdNot(nome, ignorarId);
            case TIPO_EDIFICACAO -> imovelTipoEdificacaoJpaRepository.existsByNomeAndIdNot(nome, ignorarId);
            case DESTINACAO -> imovelDestinacaoJpaRepository.existsByNomeAndIdNot(nome, ignorarId);
            case TIPO_LIMITACAO -> imovelTipoLimitacaoJpaRepository.existsByNomeAndIdNot(nome, ignorarId);
            case HABITESE_TIPO -> imovelHabiteseTipoJpaRepository.existsByNomeAndIdNot(nome, ignorarId);
        };
    }

    private static void preencherCatalogo(CatalogoIptuJpaEntityBase entidade, CatalogoIptu item, UUID tenantId) {
        if (entidade.getId() == null) {
            entidade.setId(item.id());
            entidade.setTenantId(tenantId);
        }
        entidade.setNome(item.nome());
        entidade.setAtivo(item.ativo());
    }

    private static CatalogoIptu paraDominio(CatalogoIptuJpaEntityBase entidade) {
        return new CatalogoIptu(entidade.getId(), entidade.getTenantId(), entidade.getNome(), entidade.isAtivo());
    }
}
