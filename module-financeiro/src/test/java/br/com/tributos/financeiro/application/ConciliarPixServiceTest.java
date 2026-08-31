package br.com.tributos.financeiro.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.tributos.financeiro.application.ports.GatewayPix;
import br.com.tributos.financeiro.application.ports.GatewayPix.ConsultaPixContexto;
import br.com.tributos.financeiro.application.ports.GatewayPix.PagamentoPix;
import br.com.tributos.financeiro.application.ports.GatewayPix.StatusCobrancaPix;
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
import br.com.tributos.kernel.pixbb.ConfiguracaoPixBbPort;
import br.com.tributos.kernel.pixbb.ConfiguracaoPixOperacional;
import br.com.tributos.kernel.tenancy.TenantContext;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConciliarPixServiceTest {

    private static final UUID TENANT_ID = UUID.fromString("00000000-0000-4000-8000-000000000099");

    @Mock
    private GuiaArrecadacaoRepository guiaArrecadacaoRepository;
    @Mock
    private ConfiguracaoPixBbPort configuracaoPixBbPort;
    @Mock
    private GatewayPix gatewayPix;
    @Mock
    private RegistrarPagamentoService registrarPagamentoService;
    @Mock
    private PixConciliacaoLogRepository pixConciliacaoLogRepository;

    private ConciliarPixService service;

    @BeforeEach
    void setUp() {
        service = new ConciliarPixService(
            guiaArrecadacaoRepository,
            configuracaoPixBbPort,
            gatewayPix,
            registrarPagamentoService,
            pixConciliacaoLogRepository,
            new ObjectMapper()
        );
        TenantContext.set(TENANT_ID);
    }

    @Test
    void deveBaixarGuiaQuandoBbRetornaConcluida() {
        GuiaArrecadacao guia = guiaPendente();
        GuiaArrecadacao paga = guiaPaga();
        when(guiaArrecadacaoRepository.buscarPorId(guia.id())).thenReturn(Optional.of(guia));
        when(configuracaoPixBbPort.buscarAtiva(TENANT_ID)).thenReturn(Optional.of(config()));
        when(gatewayPix.consultarPorTxid(any(), any())).thenReturn(new StatusCobrancaPix("TXID-1", "CONCLUIDA"));
        when(gatewayPix.consultarPagamentos(any(), any())).thenReturn(List.of(
            new PagamentoPix("E2E-1", "100.00", "2026-08-31T12:00:00-03:00")
        ));
        when(registrarPagamentoService.liquidacaoViaWebhook(any(), any(), any(), any()))
            .thenReturn(new ResultadoLiquidacaoPix(paga, true, false));
        when(pixConciliacaoLogRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        GuiaArrecadacao resultado = service.executar(guia.id());

        assertThat(resultado.situacao()).isEqualTo(SituacaoGuia.PAGA);
        ArgumentCaptor<PixConciliacaoLog> captor = ArgumentCaptor.forClass(PixConciliacaoLog.class);
        verify(pixConciliacaoLogRepository).salvar(captor.capture());
        assertThat(captor.getValue().origem()).isEqualTo(OrigemConciliacaoPix.CONSULTA);
        assertThat(captor.getValue().statusNovo()).isEqualTo("CONCLUIDA");
    }

    @Test
    void naoDeveAlterarGuiaQuandoStatusDesconhecido() {
        GuiaArrecadacao guia = guiaPendente();
        when(guiaArrecadacaoRepository.buscarPorId(guia.id())).thenReturn(Optional.of(guia));
        when(configuracaoPixBbPort.buscarAtiva(TENANT_ID)).thenReturn(Optional.of(config()));
        when(gatewayPix.consultarPorTxid(any(), any())).thenReturn(new StatusCobrancaPix("TXID-1", "STATUS_X"));
        when(pixConciliacaoLogRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        GuiaArrecadacao resultado = service.executar(guia.id());

        assertThat(resultado.statusPix()).isEqualTo(StatusPix.ATIVA);
        verify(pixConciliacaoLogRepository).salvar(any());
    }

    private static ConfiguracaoPixOperacional config() {
        return new ConfiguracaoPixOperacional(
            TENANT_ID, "SANDBOX", "client", "secret", "dev-key", "pix.arrecadacao-info",
            "123456", "00000000000000000000000000000000000000000000", "N", null, null, null
        );
    }

    private static GuiaArrecadacao guiaPendente() {
        return new GuiaArrecadacao(
            UUID.randomUUID(), TENANT_ID, 1L, TipoTributo.ISS, OrigemGuia.NOTA_FISCAL, UUID.randomUUID(),
            UUID.randomUUID(), 6, 2024, Instant.now(), java.time.LocalDate.now().plusDays(10),
            new BigDecimal("100.00"), SituacaoGuia.PENDENTE, null, null, null, null, "TXID-1",
            null, null, TipoTributacao.TRIBUTAVEL, StatusPix.ATIVA, "qr", "link", null, Instant.now()
        );
    }

    private static GuiaArrecadacao guiaPaga() {
        GuiaArrecadacao g = guiaPendente();
        return new GuiaArrecadacao(
            g.id(), g.tenantId(), g.numero(), g.tipoTributo(), g.origemTipo(), g.origemId(),
            g.contribuinteId(), g.competenciaMes(), g.competenciaAno(), g.dataEmissao(), g.dataVencimento(),
            g.valor(), SituacaoGuia.PAGA, g.formaPagamentoId(), Instant.now(), g.valor(),
            g.codigoBarras(), g.pixTxid(), g.descricaoAvulsa(), g.codigoVerificacao(), g.tipoTributacao(),
            StatusPix.CONCLUIDA,
            g.pixQrcodePayload(), g.pixLink(), "E2E-1", g.pixSolicitadoEm()
        );
    }
}
