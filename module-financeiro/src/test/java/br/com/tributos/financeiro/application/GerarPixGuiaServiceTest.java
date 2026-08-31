package br.com.tributos.financeiro.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.tributos.financeiro.application.ports.GatewayPix;
import br.com.tributos.financeiro.application.ports.GatewayPix.ComandoGerarQrCode;
import br.com.tributos.financeiro.application.ports.GatewayPix.RespostaQrCode;
import br.com.tributos.financeiro.domain.FormaPagamento;
import br.com.tributos.financeiro.domain.FormaPagamentoRepository;
import br.com.tributos.financeiro.domain.GuiaArrecadacao;
import br.com.tributos.financeiro.domain.GuiaArrecadacaoRepository;
import br.com.tributos.financeiro.domain.OrigemGuia;
import br.com.tributos.financeiro.domain.SituacaoGuia;
import br.com.tributos.financeiro.domain.StatusPix;
import br.com.tributos.financeiro.domain.TipoTributo;
import br.com.tributos.kernel.cadastro.DadosDevedorPix;
import br.com.tributos.kernel.cadastro.DevedorPixPort;
import br.com.tributos.kernel.pixbb.ConfiguracaoPixBbPort;
import br.com.tributos.kernel.pixbb.ConfiguracaoPixOperacional;
import br.com.tributos.kernel.pixbb.CredenciaisPixBb;
import br.com.tributos.kernel.tenancy.TenantContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GerarPixGuiaServiceTest {

    private static final UUID TENANT_ID = UUID.fromString("00000000-0000-4000-8000-000000000099");

    @Mock
    private GuiaArrecadacaoRepository guiaArrecadacaoRepository;
    @Mock
    private FormaPagamentoRepository formaPagamentoRepository;
    @Mock
    private ConfiguracaoPixBbPort configuracaoPixBbPort;
    @Mock
    private DevedorPixPort devedorPixPort;
    @Mock
    private GatewayPix gatewayPix;

    private GerarPixGuiaService service;
    private final UUID guiaId = UUID.randomUUID();
    private final UUID formaPixId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new GerarPixGuiaService(
            guiaArrecadacaoRepository,
            formaPagamentoRepository,
            configuracaoPixBbPort,
            devedorPixPort,
            gatewayPix
        );
        TenantContext.set(TENANT_ID);
    }

    @Test
    void deveSerIdempotenteQuandoPixAtivo() {
        GuiaArrecadacao guia = guiaComPixAtivo();
        when(guiaArrecadacaoRepository.buscarPorId(guiaId)).thenReturn(Optional.of(guia));

        GuiaArrecadacao resultado = service.executar(guiaId);

        assertThat(resultado.pixTxid()).isEqualTo("TXID-EXISTENTE");
        verify(gatewayPix, times(0)).gerarQrCode(any());
    }

    @Test
    void deveChamarGatewayUmaVezParaMesmaGuia() {
        GuiaArrecadacao pendente = guiaPendente();
        GuiaArrecadacao[] estado = {pendente};
        when(guiaArrecadacaoRepository.buscarPorId(guiaId)).thenAnswer(inv -> Optional.of(estado[0]));
        when(configuracaoPixBbPort.buscarAtiva(TENANT_ID)).thenReturn(Optional.of(config()));
        when(formaPagamentoRepository.buscarPorCodigo("PIX"))
            .thenReturn(Optional.of(new FormaPagamento(formaPixId, "PIX", "PIX")));
        when(devedorPixPort.buscarPorPessoaId(pendente.contribuinteId()))
            .thenReturn(Optional.of(new DadosDevedorPix("Contribuinte", "07512345678", null)));
        when(gatewayPix.gerarQrCode(any())).thenReturn(new RespostaQrCode(
            "NOVO-TXID", "qr-payload", "https://link", "ATIVA"
        ));
        when(guiaArrecadacaoRepository.salvar(any())).thenAnswer(inv -> {
            estado[0] = inv.getArgument(0);
            return estado[0];
        });

        service.executar(guiaId);
        service.executar(guiaId);

        verify(gatewayPix, times(1)).gerarQrCode(any());
    }

    @Test
    void devePersistirCamposPixAposGeracao() {
        GuiaArrecadacao pendente = guiaPendente();
        when(guiaArrecadacaoRepository.buscarPorId(guiaId)).thenReturn(Optional.of(pendente));
        when(configuracaoPixBbPort.buscarAtiva(TENANT_ID)).thenReturn(Optional.of(config()));
        when(formaPagamentoRepository.buscarPorCodigo("PIX"))
            .thenReturn(Optional.of(new FormaPagamento(formaPixId, "PIX", "PIX")));
        when(devedorPixPort.buscarPorPessoaId(pendente.contribuinteId())).thenReturn(Optional.empty());
        when(gatewayPix.gerarQrCode(any())).thenReturn(new RespostaQrCode(
            "NOVO-TXID", "qr-payload", "https://link", "ATIVA"
        ));
        when(guiaArrecadacaoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        service.executar(guiaId);

        ArgumentCaptor<GuiaArrecadacao> captor = ArgumentCaptor.forClass(GuiaArrecadacao.class);
        verify(guiaArrecadacaoRepository).salvar(captor.capture());
        GuiaArrecadacao salva = captor.getValue();
        assertThat(salva.pixTxid()).isEqualTo("NOVO-TXID");
        assertThat(salva.statusPix()).isEqualTo(StatusPix.ATIVA);
        assertThat(salva.formaPagamentoId()).isEqualTo(formaPixId);
        assertThat(salva.pixQrcodePayload()).isEqualTo("qr-payload");
    }

    private ConfiguracaoPixOperacional config() {
        return new ConfiguracaoPixOperacional(
            TENANT_ID,
            "SANDBOX",
            "client",
            "secret",
            "dev-key",
            "pix.arrecadacao-requisicao",
            "123456",
            "00000000000000000000000000000000000000000000",
            "N",
            null,
            null,
            null
        );
    }

    private GuiaArrecadacao guiaPendente() {
        return new GuiaArrecadacao(
            guiaId,
            TENANT_ID,
            42L,
            TipoTributo.ISS,
            OrigemGuia.NOTA_FISCAL,
            UUID.randomUUID(),
            UUID.randomUUID(),
            6,
            2024,
            Instant.parse("2024-06-01T12:00:00Z"),
            java.time.LocalDate.of(2024, 7, 1),
            new BigDecimal("100.00"),
            SituacaoGuia.PENDENTE,
            null,
            null,
            null,
            null,
            null,
            null,
            "CODVERIF123456789012",
            null,
            null,
            null,
            null,
            null
        );
    }

    private GuiaArrecadacao guiaComPixAtivo() {
        GuiaArrecadacao base = guiaPendente();
        return new GuiaArrecadacao(
            base.id(),
            base.tenantId(),
            base.numero(),
            base.tipoTributo(),
            base.origemTipo(),
            base.origemId(),
            base.contribuinteId(),
            base.competenciaMes(),
            base.competenciaAno(),
            base.dataEmissao(),
            base.dataVencimento(),
            base.valor(),
            base.situacao(),
            base.formaPagamentoId(),
            base.dataEfetivacao(),
            base.valorPago(),
            base.codigoBarras(),
            "TXID-EXISTENTE",
            base.descricaoAvulsa(),
            base.codigoVerificacao(),
            StatusPix.ATIVA,
            "qr",
            "link",
            null,
            Instant.now()
        );
    }
}
