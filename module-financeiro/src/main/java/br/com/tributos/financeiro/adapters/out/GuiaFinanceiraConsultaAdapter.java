package br.com.tributos.financeiro.adapters.out;

import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.financeiro.domain.GuiaArrecadacaoRepository;
import br.com.tributos.financeiro.domain.OrigemGuia;
import br.com.tributos.financeiro.domain.SituacaoGuia;
import br.com.tributos.kernel.financeiro.GuiaFinanceiraConsultaPort;

@Component
public class GuiaFinanceiraConsultaAdapter implements GuiaFinanceiraConsultaPort {

    private final GuiaArrecadacaoRepository guiaArrecadacaoRepository;

    public GuiaFinanceiraConsultaAdapter(GuiaArrecadacaoRepository guiaArrecadacaoRepository) {
        this.guiaArrecadacaoRepository = guiaArrecadacaoRepository;
    }

    @Override
    public boolean origemEstaPaga(UUID tenantId, String origemTipo, UUID origemId) {
        OrigemGuia origem;
        try {
            origem = OrigemGuia.valueOf(origemTipo);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return guiaArrecadacaoRepository.buscarPorOrigem(origem, origemId)
            .map(g -> g.situacao() == SituacaoGuia.PAGA)
            .orElse(false);
    }
}
