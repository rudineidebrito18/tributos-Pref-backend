package br.com.tributos.financeiro.application;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import br.com.tributos.financeiro.domain.GuiaArrecadacao;
import br.com.tributos.financeiro.domain.GuiaArrecadacaoRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class ConciliacaoPixLoteService {

    private final GuiaArrecadacaoRepository guiaArrecadacaoRepository;
    private final ConciliarPixService conciliarPixService;
    private final ConciliacaoPixProperties properties;

    public ConciliacaoPixLoteService(
        GuiaArrecadacaoRepository guiaArrecadacaoRepository,
        ConciliarPixService conciliarPixService,
        ConciliacaoPixProperties properties
    ) {
        this.guiaArrecadacaoRepository = guiaArrecadacaoRepository;
        this.conciliarPixService = conciliarPixService;
        this.properties = properties;
    }

    public int executarParaTenant(UUID tenantId) {
        TenantContext.set(tenantId);
        try {
            Instant desde = Instant.now().minus(properties.diasRetroativos(), ChronoUnit.DAYS);
            List<GuiaArrecadacao> guias = guiaArrecadacaoRepository.buscarAtivasParaConciliacao(
                desde,
                PageRequest.of(0, properties.tamanhoLote())
            );
            int processadas = 0;
            for (GuiaArrecadacao guia : guias) {
                conciliarPixService.executar(guia.id());
                processadas++;
            }
            return processadas;
        } finally {
            TenantContext.clear();
        }
    }
}
