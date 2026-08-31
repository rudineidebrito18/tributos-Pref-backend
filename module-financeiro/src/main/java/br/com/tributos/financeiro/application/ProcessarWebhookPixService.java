package br.com.tributos.financeiro.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.financeiro.application.webhook.PixRecebidoComando;
import br.com.tributos.financeiro.application.webhook.WebhookPixComando;
import br.com.tributos.financeiro.domain.GuiaArrecadacao;
import br.com.tributos.financeiro.domain.GuiaArrecadacaoRepository;
import br.com.tributos.financeiro.domain.OrigemConciliacaoPix;
import br.com.tributos.financeiro.domain.PixConciliacaoLog;
import br.com.tributos.financeiro.domain.PixConciliacaoLogRepository;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class ProcessarWebhookPixService {

    private static final Logger log = LoggerFactory.getLogger(ProcessarWebhookPixService.class);

    private final GuiaArrecadacaoRepository guiaArrecadacaoRepository;
    private final RegistrarPagamentoService registrarPagamentoService;
    private final PixConciliacaoLogRepository pixConciliacaoLogRepository;

    public ProcessarWebhookPixService(
        GuiaArrecadacaoRepository guiaArrecadacaoRepository,
        RegistrarPagamentoService registrarPagamentoService,
        PixConciliacaoLogRepository pixConciliacaoLogRepository
    ) {
        this.guiaArrecadacaoRepository = guiaArrecadacaoRepository;
        this.registrarPagamentoService = registrarPagamentoService;
        this.pixConciliacaoLogRepository = pixConciliacaoLogRepository;
    }

    @Transactional
    public void processar(UUID tenantId, WebhookPixComando payload, String payloadBruto) {
        TenantContext.set(tenantId);
        try {
            List<PixRecebidoComando> itens = payload != null && payload.pix() != null ? payload.pix() : List.of();
            for (PixRecebidoComando item : itens) {
                processarItem(tenantId, item, payloadBruto);
            }
        } finally {
            TenantContext.clear();
        }
    }

    private void processarItem(UUID tenantId, PixRecebidoComando item, String payloadBruto) {
        if (item.txid() == null || item.txid().isBlank()) {
            return;
        }
        Optional<GuiaArrecadacao> guiaOpt = guiaArrecadacaoRepository.buscarPorPixTxid(tenantId, item.txid());
        if (guiaOpt.isEmpty()) {
            gravarLog(
                tenantId,
                null,
                item.txid(),
                item.endToEndId(),
                null,
                "ORFAO",
                payloadBruto
            );
            log.warn("Webhook PIX com txid desconhecido no tenant {}: {}", tenantId, item.txid());
            return;
        }

        GuiaArrecadacao guia = guiaOpt.get();
        String statusAnterior = guia.situacao().name();
        BigDecimal valorRecebido = resolverValor(item);
        Instant horario = item.horario() != null ? item.horario().toInstant() : Instant.now();

        ResultadoLiquidacaoPix resultado = registrarPagamentoService.liquidacaoViaWebhook(
            guia,
            valorRecebido,
            item.endToEndId(),
            horario
        );

        if (resultado.idempotente()) {
            return;
        }

        if (!resultado.valorCompleto()) {
            log.warn(
                "Webhook PIX com valor parcial na guia {}: recebido={}, esperado={}",
                guia.id(),
                valorRecebido,
                guia.valor()
            );
        }

        String statusNovo = resultado.guia().situacao().name();
        gravarLog(
            tenantId,
            resultado.guia().id(),
            item.txid(),
            item.endToEndId(),
            statusAnterior,
            statusNovo,
            payloadBruto
        );
    }

    private static BigDecimal resolverValor(PixRecebidoComando item) {
        if (item.valor() != null) {
            return item.valor();
        }
        if (item.componentesValor() != null
            && item.componentesValor().original() != null
            && item.componentesValor().original().valor() != null) {
            return item.componentesValor().original().valor();
        }
        throw new IllegalArgumentException("Valor do PIX ausente no webhook.");
    }

    private void gravarLog(
        UUID tenantId,
        UUID guiaId,
        String txid,
        String endToEndId,
        String statusAnterior,
        String statusNovo,
        String payloadBruto
    ) {
        pixConciliacaoLogRepository.salvar(new PixConciliacaoLog(
            null,
            tenantId,
            guiaId,
            txid,
            endToEndId,
            statusAnterior,
            statusNovo,
            OrigemConciliacaoPix.WEBHOOK,
            payloadBruto,
            Instant.now()
        ));
    }
}
