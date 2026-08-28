package br.com.tributos.iptu.adapters.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iptu.domain.LancamentoParcela;
import br.com.tributos.iptu.domain.LancamentoParcelaRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Component
public class LancamentoParcelaRepositoryAdapter implements LancamentoParcelaRepository {

    private final LancamentoParcelaJpaRepository jpaRepository;

    public LancamentoParcelaRepositoryAdapter(LancamentoParcelaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<LancamentoParcela> salvarTodos(List<LancamentoParcela> parcelas) {
        UUID tenantId = TenantContext.getObrigatorio();
        List<LancamentoParcelaJpaEntity> entidades = parcelas.stream().map(parcela -> {
            LancamentoParcelaJpaEntity entidade = new LancamentoParcelaJpaEntity();
            entidade.setId(parcela.id());
            entidade.setTenantId(tenantId);
            entidade.setLancamentoId(parcela.lancamentoId());
            entidade.setNumeroParcela(parcela.numeroParcela());
            entidade.setValor(parcela.valor());
            entidade.setVencimento(parcela.vencimento());
            entidade.setStatus(parcela.status());
            return entidade;
        }).toList();

        return jpaRepository.saveAll(entidades).stream().map(LancamentoParcelaRepositoryAdapter::paraDominio).toList();
    }

    @Override
    public List<LancamentoParcela> listarPorLancamento(UUID lancamentoId) {
        return jpaRepository.findByLancamentoIdOrderByNumeroParcelaAsc(lancamentoId).stream()
            .map(LancamentoParcelaRepositoryAdapter::paraDominio)
            .toList();
    }

    private static LancamentoParcela paraDominio(LancamentoParcelaJpaEntity entidade) {
        return new LancamentoParcela(
            entidade.getId(),
            entidade.getTenantId(),
            entidade.getLancamentoId(),
            entidade.getNumeroParcela(),
            entidade.getValor(),
            entidade.getVencimento(),
            entidade.getStatus()
        );
    }
}
