package br.com.tributos.iptu.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import br.com.tributos.iptu.domain.LancamentoIptu;
import br.com.tributos.iptu.domain.LancamentoIptuRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class LancamentoIptuRepositoryAdapter implements LancamentoIptuRepository {

    private final LancamentoIptuJpaRepository jpaRepository;

    public LancamentoIptuRepositoryAdapter(LancamentoIptuJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public LancamentoIptu salvar(LancamentoIptu lancamento) {
        UUID tenantId = TenantContext.getObrigatorio();
        LancamentoIptuJpaEntity entidade = jpaRepository.findById(lancamento.id())
            .orElseGet(() -> {
                LancamentoIptuJpaEntity nova = new LancamentoIptuJpaEntity();
                nova.setId(lancamento.id());
                nova.setTenantId(tenantId);
                return nova;
            });

        entidade.setImovelId(lancamento.imovelId());
        entidade.setExercicio(lancamento.exercicio());
        entidade.setValorVenalCalculado(lancamento.valorVenalCalculado());
        entidade.setAliquotaAplicada(lancamento.aliquotaAplicada());
        entidade.setValorTotal(lancamento.valorTotal());
        entidade.setNumeroParcelas(lancamento.numeroParcelas());
        entidade.setStatus(lancamento.status());

        LancamentoIptuJpaEntity salva = jpaRepository.save(entidade);
        return paraDominio(salva, lancamento.dataGeracao());
    }

    @Override
    public Optional<LancamentoIptu> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(LancamentoIptuRepositoryAdapter::paraDominio);
    }

    @Override
    public Optional<LancamentoIptu> buscarPorImovelEExercicio(UUID imovelId, int exercicio) {
        return jpaRepository.findByImovelIdAndExercicio(imovelId, exercicio).map(LancamentoIptuRepositoryAdapter::paraDominio);
    }

    @Override
    public Page<LancamentoIptu> listar(Integer exercicio, UUID imovelId, Pageable pageable) {
        return jpaRepository.buscarComFiltro(exercicio, imovelId, pageable)
            .map(LancamentoIptuRepositoryAdapter::paraDominio);
    }

    private static LancamentoIptu paraDominio(LancamentoIptuJpaEntity entidade, java.time.Instant fallbackDataGeracao) {
        java.time.Instant dataGeracao = entidade.getDataGeracao() != null ? entidade.getDataGeracao() : fallbackDataGeracao;
        return new LancamentoIptu(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getImovelId(),
            entidade.getExercicio(),
            entidade.getValorVenalCalculado(),
            entidade.getAliquotaAplicada(),
            entidade.getValorTotal(),
            entidade.getNumeroParcelas(),
            entidade.getStatus(),
            dataGeracao
        );
    }

    private static LancamentoIptu paraDominio(LancamentoIptuJpaEntity entidade) {
        return paraDominio(entidade, null);
    }
}
