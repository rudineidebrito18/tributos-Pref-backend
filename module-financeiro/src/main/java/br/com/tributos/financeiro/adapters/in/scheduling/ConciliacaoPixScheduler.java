package br.com.tributos.financeiro.adapters.in.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.tributos.financeiro.application.ConciliacaoPixLoteService;
import br.com.tributos.financeiro.application.ConciliacaoPixProperties;
import br.com.tributos.kernel.tenancy.ListarTenantsAtivosPort;

@Component
@ConditionalOnProperty(name = "app.pix.conciliacao.habilitada", havingValue = "true", matchIfMissing = true)
public class ConciliacaoPixScheduler {

    private static final Logger log = LoggerFactory.getLogger(ConciliacaoPixScheduler.class);

    private final ListarTenantsAtivosPort listarTenantsAtivosPort;
    private final ConciliacaoPixLoteService conciliacaoPixLoteService;
    private final ConciliacaoPixProperties properties;

    public ConciliacaoPixScheduler(
        ListarTenantsAtivosPort listarTenantsAtivosPort,
        ConciliacaoPixLoteService conciliacaoPixLoteService,
        ConciliacaoPixProperties properties
    ) {
        this.listarTenantsAtivosPort = listarTenantsAtivosPort;
        this.conciliacaoPixLoteService = conciliacaoPixLoteService;
        this.properties = properties;
    }

    @Scheduled(cron = "${app.pix.conciliacao.cron:0 */15 * * * *}")
    public void conciliarGuiasAtivas() {
        if (!properties.habilitada()) {
            return;
        }
        for (var tenantId : listarTenantsAtivosPort.listarIds()) {
            try {
                int processadas = conciliacaoPixLoteService.executarParaTenant(tenantId);
                if (processadas > 0) {
                    log.info("Conciliação PIX: {} guia(s) processada(s) no tenant {}", processadas, tenantId);
                }
            } catch (RuntimeException ex) {
                log.warn("Falha na conciliação PIX do tenant {}: {}", tenantId, ex.getMessage());
            }
        }
    }
}
