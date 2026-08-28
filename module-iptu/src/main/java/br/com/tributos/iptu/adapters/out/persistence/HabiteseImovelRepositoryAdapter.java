package br.com.tributos.iptu.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import br.com.tributos.iptu.domain.HabiteseImovel;
import br.com.tributos.iptu.domain.HabiteseImovelRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class HabiteseImovelRepositoryAdapter implements HabiteseImovelRepository {

    private final HabiteseImovelJpaRepository jpaRepository;

    public HabiteseImovelRepositoryAdapter(HabiteseImovelJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public HabiteseImovel salvar(HabiteseImovel habitese) {
        UUID tenantId = TenantContext.getObrigatorio();
        HabiteseImovelJpaEntity entidade = jpaRepository.findById(habitese.id())
            .orElseGet(() -> {
                HabiteseImovelJpaEntity nova = new HabiteseImovelJpaEntity();
                nova.setId(habitese.id());
                nova.setTenantId(tenantId);
                return nova;
            });

        entidade.setImovelId(habitese.imovelId());
        entidade.setTipoId(habitese.tipoId());
        entidade.setNumero(habitese.numero());
        entidade.setDataEmissao(habitese.dataEmissao());

        HabiteseImovelJpaEntity salva = jpaRepository.save(entidade);
        return paraDominio(salva, habitese.dataEmissaoTs());
    }

    @Override
    public Optional<HabiteseImovel> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(HabiteseImovelRepositoryAdapter::paraDominio);
    }

    @Override
    public Page<HabiteseImovel> listarPorImovel(UUID imovelId, Pageable pageable) {
        return jpaRepository.findByImovelIdOrderByNumeroDesc(imovelId, pageable)
            .map(HabiteseImovelRepositoryAdapter::paraDominio);
    }

    @Override
    public long proximoNumero() {
        return jpaRepository.findMaxNumero() + 1;
    }

    private static HabiteseImovel paraDominio(HabiteseImovelJpaEntity entidade, java.time.Instant fallbackTs) {
        java.time.Instant ts = entidade.getDataEmissaoTs() != null ? entidade.getDataEmissaoTs() : fallbackTs;
        return new HabiteseImovel(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getImovelId(),
            entidade.getTipoId(),
            entidade.getNumero(),
            entidade.getDataEmissao(),
            ts
        );
    }

    private static HabiteseImovel paraDominio(HabiteseImovelJpaEntity entidade) {
        return paraDominio(entidade, null);
    }
}
