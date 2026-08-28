package br.com.tributos.financeiro.adapters.out;

import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.financeiro.domain.GuiaArrecadacaoRepository;
import br.com.tributos.kernel.financeiro.PendenciaFinanceiraPort;

@Component
public class PendenciaFinanceiraAdapter implements PendenciaFinanceiraPort {

    private final GuiaArrecadacaoRepository guiaArrecadacaoRepository;

    public PendenciaFinanceiraAdapter(GuiaArrecadacaoRepository guiaArrecadacaoRepository) {
        this.guiaArrecadacaoRepository = guiaArrecadacaoRepository;
    }

    @Override
    public boolean possuiPendencia(UUID tenantId, UUID pessoaId) {
        return guiaArrecadacaoRepository.possuiPendencia(tenantId, pessoaId);
    }
}
