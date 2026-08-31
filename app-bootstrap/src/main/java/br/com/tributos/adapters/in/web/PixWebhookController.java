package br.com.tributos.adapters.in.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.tributos.adapters.in.web.dto.pixbb.PixRecebido;
import br.com.tributos.adapters.in.web.dto.pixbb.WebhookPixPayload;
import br.com.tributos.financeiro.application.ProcessarWebhookPixService;
import br.com.tributos.financeiro.application.ValidadorAutenticacaoWebhookPix;
import br.com.tributos.financeiro.application.webhook.ComponenteComando;
import br.com.tributos.financeiro.application.webhook.ComponentesValorComando;
import br.com.tributos.financeiro.application.webhook.PagadorComando;
import br.com.tributos.financeiro.application.webhook.PixRecebidoComando;
import br.com.tributos.financeiro.application.webhook.WebhookPixComando;
import br.com.tributos.identity.application.BuscarTenantPorSlugService;
import br.com.tributos.identity.domain.Tenant;
import tools.jackson.databind.ObjectMapper;

/**
 * Webhook público de liquidação PIX do Banco do Brasil — autenticado por mTLS ou token compartilhado.
 */
@RestController
@RequestMapping("/api/webhooks/pix")
public class PixWebhookController {

    private final BuscarTenantPorSlugService buscarTenantPorSlugService;
    private final ValidadorAutenticacaoWebhookPix validadorAutenticacaoWebhookPix;
    private final ProcessarWebhookPixService processarWebhookPixService;
    private final ObjectMapper objectMapper;

    public PixWebhookController(
        BuscarTenantPorSlugService buscarTenantPorSlugService,
        ValidadorAutenticacaoWebhookPix validadorAutenticacaoWebhookPix,
        ProcessarWebhookPixService processarWebhookPixService,
        ObjectMapper objectMapper
    ) {
        this.buscarTenantPorSlugService = buscarTenantPorSlugService;
        this.validadorAutenticacaoWebhookPix = validadorAutenticacaoWebhookPix;
        this.processarWebhookPixService = processarWebhookPixService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/{tenantSlug}")
    public ResponseEntity<Void> receber(
        @PathVariable String tenantSlug,
        @RequestHeader(value = "X-Webhook-Token", required = false) String webhookToken,
        @RequestHeader(value = "X-Client-Cert-Verified", required = false) String certificadoVerificado,
        @RequestHeader(value = "Authorization", required = false) String authorization,
        @RequestBody WebhookPixPayload payload
    ) {
        Tenant tenant = buscarTenantPorSlugService.executar(tenantSlug);
        if (!validadorAutenticacaoWebhookPix.validar(
            tenant.getId(),
            certificadoVerificado,
            webhookToken,
            authorization
        )) {
            return ResponseEntity.status(401).build();
        }

        String payloadBruto = serializarPayload(payload);
        processarWebhookPixService.processar(tenant.getId(), paraComando(payload), payloadBruto);
        return ResponseEntity.ok().build();
    }

    private String serializarPayload(WebhookPixPayload payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            return "{}";
        }
    }

    private static WebhookPixComando paraComando(WebhookPixPayload payload) {
        if (payload == null || payload.pix() == null) {
            return new WebhookPixComando(List.of());
        }
        List<PixRecebidoComando> itens = payload.pix().stream().map(PixWebhookController::paraComandoItem).toList();
        return new WebhookPixComando(itens);
    }

    private static PixRecebidoComando paraComandoItem(PixRecebido item) {
        ComponentesValorComando componentes = null;
        if (item.componentesValor() != null) {
            var original = item.componentesValor().original();
            componentes = new ComponentesValorComando(
                original != null ? new ComponenteComando(original.valor()) : null
            );
        }
        PagadorComando pagador = null;
        if (item.pagador() != null) {
            pagador = new PagadorComando(item.pagador().cpf(), item.pagador().cnpj(), item.pagador().nome());
        }
        return new PixRecebidoComando(
            item.endToEndId(),
            item.txid(),
            item.valor(),
            componentes,
            item.chave(),
            item.horario(),
            item.infoPagador(),
            pagador
        );
    }
}
