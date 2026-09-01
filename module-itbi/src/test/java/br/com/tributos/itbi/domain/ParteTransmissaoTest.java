package br.com.tributos.itbi.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.tributos.kernel.exception.RegraNegocioException;

class ParteTransmissaoTest {

    private static final UUID TENANT_ID = UUID.randomUUID();
    private static final UUID GUIA_ID = UUID.randomUUID();

    @Test
    void deveAceitarDoisAdquirentesComCinquentaPorCentoEUmPrincipal() {
        List<ParteTransmissao> adquirentes = List.of(
            parte(UUID.randomUUID(), PapelParteTransmissao.ADQUIRENTE, new BigDecimal("50"), true),
            parte(UUID.randomUUID(), PapelParteTransmissao.ADQUIRENTE, new BigDecimal("50"), false)
        );

        assertThatCode(() -> ParteTransmissao.validarComposicao(adquirentes))
            .doesNotThrowAnyException();
    }

    @Test
    void deveRejeitarSomaDiferenteDeCem() {
        List<ParteTransmissao> adquirentes = List.of(
            parte(UUID.randomUUID(), PapelParteTransmissao.ADQUIRENTE, new BigDecimal("60"), true),
            parte(UUID.randomUUID(), PapelParteTransmissao.ADQUIRENTE, new BigDecimal("50"), false)
        );

        assertThatThrownBy(() -> ParteTransmissao.validarComposicao(adquirentes))
            .isInstanceOf(RegraNegocioException.class)
            .hasMessageContaining("100%");
    }

    @Test
    void deveRejeitarQuandoHaDoisPrincipais() {
        List<ParteTransmissao> adquirentes = List.of(
            parte(UUID.randomUUID(), PapelParteTransmissao.ADQUIRENTE, new BigDecimal("50"), true),
            parte(UUID.randomUUID(), PapelParteTransmissao.ADQUIRENTE, new BigDecimal("50"), true)
        );

        assertThatThrownBy(() -> ParteTransmissao.validarComposicao(adquirentes))
            .isInstanceOf(RegraNegocioException.class)
            .hasMessageContaining("principal");
    }

    @Test
    void deveRejeitarGuiaSemTransmitente() {
        List<ParteTransmissao> adquirentes = List.of(
            parte(UUID.randomUUID(), PapelParteTransmissao.ADQUIRENTE, new BigDecimal("100"), true)
        );

        assertThatThrownBy(() -> ParteTransmissao.validarGuiaCompleta(List.of(), adquirentes))
            .isInstanceOf(RegraNegocioException.class)
            .hasMessageContaining("transmitente");
    }

    private static ParteTransmissao parte(
        UUID contribuinteId,
        PapelParteTransmissao papel,
        BigDecimal porcentagem,
        boolean principal
    ) {
        return new ParteTransmissao(
            UUID.randomUUID(),
            TENANT_ID,
            GUIA_ID,
            contribuinteId,
            papel,
            porcentagem,
            principal
        );
    }
}
