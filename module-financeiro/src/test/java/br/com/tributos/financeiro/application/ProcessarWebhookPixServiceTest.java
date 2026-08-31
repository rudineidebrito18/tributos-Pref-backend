package br.com.tributos.financeiro.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.tributos.financeiro.application.webhook.ComponenteComando;
import br.com.tributos.financeiro.application.webhook.ComponentesValorComando;
import br.com.tributos.financeiro.application.webhook.PixRecebidoComando;
import br.com.tributos.financeiro.application.webhook.WebhookPixComando;
import br.com.tributos.financeiro.domain.GuiaArrecadacao;
import br.com.tributos.financeiro.domain.GuiaArrecadacaoRepository;
import br.com.tributos.financeiro.domain.OrigemConciliacaoPix;
import br.com.tributos.financeiro.domain.OrigemGuia;
import br.com.tributos.financeiro.domain.PixConciliacaoLog;
import br.com.tributos.financeiro.domain.PixConciliacaoLogRepository;
import br.com.tributos.financeiro.domain.SituacaoGuia;
import br.com.tributos.financeiro.domain.StatusPix;
import br.com.tributos.financeiro.domain.TipoTributacao;
import br.com.tributos.financeiro.domain.TipoTributo;
import br.com.tributos.kernel.tenancy.TenantContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessarWebhookPixServiceTest {

    private static final UUID TENANT_ID = UUID.fromString("00000000-0000-4000-8000-000000000099");

    @Mock
    private GuiaArrecadacaoRepository guiaArrecadacaoRepository;
    @Mock
    private RegistrarPagamentoService registrarPagamentoService;
    @Mock
    private PixConciliacaoLogRepository pixConciliacaoLogRepository;

    private ProcessarWebhookPixService service;

    @BeforeEach
    void setUp() {
        service = new ProcessarWebhookPixService(
            guiaArrecadacaoRepository,
            registrarPagamentoService,
            pixConciliacaoLogRepository
        );
    }

    @Test
    void deveRegistrarLogOrfaoQuandoTxidDesconhecido() {
        WebhookPixComando payload = new WebhookPixComando(java.util.List.of(
            new PixRecebidoComando(
                "E2E-ORFAO",
                "txid-inexistente",
                new BigDecimal("50.00"),
                null,
                null,
                OffsetDateTime.parse("2022-07-27T14:30:47.00-03:00"),
                null,
                null
            )
        ));
        when(guiaArrecadacaoRepository.buscarPorPixTxid(TENANT_ID, "txid-inexistente"))
            .thenReturn(Optional.empty());
        when(pixConciliacaoLogRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        service.processar(TENANT_ID, payload, "{}");

        ArgumentCaptor<PixConciliacaoLog> captor = ArgumentCaptor.forClass(PixConciliacaoLog.class);
        verify(pixConciliacaoLogRepository).salvar(captor.capture());
        PixConciliacaoLog log = captor.getValue();
        assertThat(log.guiaId()).isNull();
        assertThat(log.txid()).isEqualTo("txid-inexistente");
        assertThat(log.statusNovo()).isEqualTo("ORFAO");
        assertThat(log.origem()).isEqualTo(OrigemConciliacaoPix.WEBHOOK);
        verify(registrarPagamentoService, never()).liquidacaoViaWebhook(any(), any(), any(), any());
        assertThat(TenantContext.get()).isNull();
    }

    @Test
    void deveSerIdempotenteQuandoMesmoEndToEndId() {
        GuiaArrecadacao guiaPaga = guia(new BigDecimal("100.00"), SituacaoGuia.PAGA, "TXID-1", "E2E-1");
        WebhookPixComando payload = payloadComTxid("TXID-1", "E2E-1", "100.00");

        when(guiaArrecadacaoRepository.buscarPorPixTxid(TENANT_ID, "TXID-1")).thenReturn(Optional.of(guiaPaga));
        when(registrarPagamentoService.liquidacaoViaWebhook(any(), any(), any(), any()))
            .thenReturn(new ResultadoLiquidacaoPix(guiaPaga, true, true));

        service.processar(TENANT_ID, payload, "{}");

        verify(pixConciliacaoLogRepository, never()).salvar(any());
    }

    @Test
    void deveBaixarGuiaComValorCompleto() {
        GuiaArrecadacao pendente = guia(new BigDecimal("100.00"), SituacaoGuia.PENDENTE, "TXID-2", null);
        GuiaArrecadacao paga = guia(new BigDecimal("100.00"), SituacaoGuia.PAGA, "TXID-2", "E2E-2");
        WebhookPixComando payload = payloadComTxid("TXID-2", "E2E-2", "100.00");

        when(guiaArrecadacaoRepository.buscarPorPixTxid(TENANT_ID, "TXID-2")).thenReturn(Optional.of(pendente));
        when(registrarPagamentoService.liquidacaoViaWebhook(any(), any(), any(), any()))
            .thenReturn(new ResultadoLiquidacaoPix(paga, true, false));
        when(pixConciliacaoLogRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        service.processar(TENANT_ID, payload, "{}");

        verify(registrarPagamentoService, times(1)).liquidacaoViaWebhook(
            pendente,
            new BigDecimal("100.00"),
            "E2E-2",
            payload.pix().get(0).horario().toInstant()
        );
        verify(pixConciliacaoLogRepository).salvar(any());
    }

    private static WebhookPixComando payloadComTxid(String txid, String endToEndId, String valor) {
        return new WebhookPixComando(java.util.List.of(
            new PixRecebidoComando(
                endToEndId,
                txid,
                new BigDecimal(valor),
                new ComponentesValorComando(new ComponenteComando(new BigDecimal(valor))),
                null,
                OffsetDateTime.parse("2022-07-27T14:30:47.00-03:00"),
                null,
                null
            )
        ));
    }

    private static GuiaArrecadacao guia(BigDecimal valor, SituacaoGuia situacao, String txid, String endToEndId) {
        return new GuiaArrecadacao(
            UUID.randomUUID(),
            TENANT_ID,
            1L,
            TipoTributo.ISS,
            OrigemGuia.NOTA_FISCAL,
            UUID.randomUUID(),
            UUID.randomUUID(),
            6,
            2024,
            Instant.now(),
            java.time.LocalDate.now().plusDays(10),
            valor,
            situacao,
            null,
            situacao == SituacaoGuia.PAGA ? Instant.now() : null,
            situacao == SituacaoGuia.PAGA ? valor : null,
            null,
            txid,
            null,
            null,
            TipoTributacao.TRIBUTAVEL,
            StatusPix.ATIVA,
            "qr",
            "link",
            endToEndId,
            Instant.now()
        );
    }
}
