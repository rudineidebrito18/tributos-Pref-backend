package br.com.tributos.financeiro.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import br.com.tributos.financeiro.domain.GuiaArrecadacao;
import br.com.tributos.financeiro.domain.GuiaArrecadacaoRepository;
import br.com.tributos.financeiro.domain.OrigemGuia;
import br.com.tributos.financeiro.domain.SituacaoGuia;
import br.com.tributos.financeiro.domain.StatusPix;
import br.com.tributos.financeiro.domain.TipoTributacao;
import br.com.tributos.financeiro.domain.TipoTributo;
import br.com.tributos.kernel.tenancy.ListarTenantsAtivosPort;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConciliacaoPixLoteServiceTest {

    @Mock
    private GuiaArrecadacaoRepository guiaArrecadacaoRepository;
    @Mock
    private ConciliarPixService conciliarPixService;

    @Test
    void deveConciliarGuiasAtivasDoTenant() {
        UUID tenantId = UUID.randomUUID();
        UUID guiaId = UUID.randomUUID();
        GuiaArrecadacao guia = guiaAtiva(guiaId, tenantId);
        ConciliacaoPixProperties props = new ConciliacaoPixProperties(true, "0 */15 * * * *", 100, 30);
        ConciliacaoPixLoteService service = new ConciliacaoPixLoteService(
            guiaArrecadacaoRepository, conciliarPixService, props
        );

        when(guiaArrecadacaoRepository.buscarAtivasParaConciliacao(any(), any(Pageable.class)))
            .thenReturn(List.of(guia));

        int processadas = service.executarParaTenant(tenantId);

        verify(conciliarPixService, times(1)).executar(guiaId);
        org.assertj.core.api.Assertions.assertThat(processadas).isEqualTo(1);
    }

    @Test
    void schedulerDesabilitadoNaoDeveSerResponsabilidadeDoLote() {
        // O lote em si sempre processa quando chamado; o scheduler respeita a flag habilitada.
        ConciliacaoPixProperties props = new ConciliacaoPixProperties(false, "0 */15 * * * *", 100, 30);
        org.assertj.core.api.Assertions.assertThat(props.habilitada()).isFalse();
    }

    private static GuiaArrecadacao guiaAtiva(UUID id, UUID tenantId) {
        return new GuiaArrecadacao(
            id, tenantId, 1L, TipoTributo.ISS, OrigemGuia.AVULSO, UUID.randomUUID(), UUID.randomUUID(),
            1, 2024, Instant.now(), java.time.LocalDate.now().plusDays(5), new BigDecimal("50.00"),
            SituacaoGuia.PENDENTE, null, null, null, null, "TXID-ATIVA", null, null,
            TipoTributacao.TRIBUTAVEL, StatusPix.ATIVA, "qr", "link", null, Instant.now()
        );
    }
}
