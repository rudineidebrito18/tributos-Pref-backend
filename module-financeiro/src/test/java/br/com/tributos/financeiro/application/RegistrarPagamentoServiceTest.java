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

import br.com.tributos.financeiro.domain.FormaPagamento;
import br.com.tributos.financeiro.domain.FormaPagamentoRepository;
import br.com.tributos.financeiro.domain.GuiaArrecadacao;
import br.com.tributos.financeiro.domain.GuiaArrecadacaoRepository;
import br.com.tributos.financeiro.domain.OrigemGuia;
import br.com.tributos.financeiro.domain.SituacaoGuia;
import br.com.tributos.financeiro.domain.StatusPix;
import br.com.tributos.financeiro.domain.TipoTributo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarPagamentoServiceTest {

    @Mock
    private GuiaArrecadacaoRepository guiaArrecadacaoRepository;

    @Mock
    private FormaPagamentoRepository formaPagamentoRepository;

    private RegistrarPagamentoService service;

    private final UUID guiaId = UUID.randomUUID();
    private final UUID formaBaixaManualId = UUID.randomUUID();
    private final UUID formaPixId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new RegistrarPagamentoService(guiaArrecadacaoRepository, formaPagamentoRepository);
    }

    @Test
    void baixaManualDeveGravarAtualizacaoManualEBaixaManual() {
        GuiaArrecadacao pendente = guiaPendente();
        when(guiaArrecadacaoRepository.buscarPorId(guiaId)).thenReturn(Optional.of(pendente));
        when(formaPagamentoRepository.buscarPorCodigo(RegistrarPagamentoService.CODIGO_FORMA_BAIXA_MANUAL))
            .thenReturn(Optional.of(new FormaPagamento(formaBaixaManualId, "BAIXA_MANUAL", "Baixa manual")));
        when(guiaArrecadacaoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        service.baixaManual(guiaId, new BigDecimal("150.00"));

        ArgumentCaptor<GuiaArrecadacao> captor = ArgumentCaptor.forClass(GuiaArrecadacao.class);
        verify(guiaArrecadacaoRepository).salvar(captor.capture());
        GuiaArrecadacao salva = captor.getValue();
        assertThat(salva.situacao()).isEqualTo(SituacaoGuia.PAGA);
        assertThat(salva.statusPix()).isEqualTo(StatusPix.ATUALIZACAO_MANUAL);
        assertThat(salva.formaPagamentoId()).isEqualTo(formaBaixaManualId);
        assertThat(salva.valorPago()).isEqualByComparingTo("150.00");
    }

    @Test
    void simularPixDeveGravarStatusAtivaEPayload() {
        GuiaArrecadacao pendente = guiaPendente();
        when(guiaArrecadacaoRepository.buscarPorId(guiaId)).thenReturn(Optional.of(pendente));
        when(guiaArrecadacaoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        service.simularPix(guiaId);

        ArgumentCaptor<GuiaArrecadacao> captor = ArgumentCaptor.forClass(GuiaArrecadacao.class);
        verify(guiaArrecadacaoRepository).salvar(captor.capture());
        GuiaArrecadacao salva = captor.getValue();
        assertThat(salva.situacao()).isEqualTo(SituacaoGuia.PENDENTE);
        assertThat(salva.statusPix()).isEqualTo(StatusPix.ATIVA);
        assertThat(salva.pixTxid()).startsWith("MOCK-");
        assertThat(salva.pixQrcodePayload()).contains(salva.pixTxid());
        assertThat(salva.pixSolicitadoEm()).isNotNull();
    }

    @Test
    void confirmarPixDeveGravarConcluidaEFormaPix() {
        GuiaArrecadacao comPix = comPixSimulado();
        when(guiaArrecadacaoRepository.buscarPorId(guiaId)).thenReturn(Optional.of(comPix));
        when(formaPagamentoRepository.buscarPorCodigo(RegistrarPagamentoService.CODIGO_FORMA_PIX))
            .thenReturn(Optional.of(new FormaPagamento(formaPixId, "PIX", "PIX")));
        when(guiaArrecadacaoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        service.confirmarPix(guiaId);

        ArgumentCaptor<GuiaArrecadacao> captor = ArgumentCaptor.forClass(GuiaArrecadacao.class);
        verify(guiaArrecadacaoRepository).salvar(captor.capture());
        GuiaArrecadacao salva = captor.getValue();
        assertThat(salva.situacao()).isEqualTo(SituacaoGuia.PAGA);
        assertThat(salva.statusPix()).isEqualTo(StatusPix.CONCLUIDA);
        assertThat(salva.formaPagamentoId()).isEqualTo(formaPixId);
    }

    private GuiaArrecadacao guiaPendente() {
        return new GuiaArrecadacao(
            guiaId,
            UUID.randomUUID(),
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
            null,
            null,
            null,
            null,
            null
        );
    }

    private GuiaArrecadacao comPixSimulado() {
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
            "23793.38128",
            "MOCK-txid",
            base.descricaoAvulsa(),
            StatusPix.ATIVA,
            "qr-payload",
            null,
            null,
            Instant.now()
        );
    }
}
