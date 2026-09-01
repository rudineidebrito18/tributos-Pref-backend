package br.com.tributos.iptu.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.tributos.kernel.exception.RegraNegocioException;

class ImovelProprietarioTest {

    private static final UUID TENANT_ID = UUID.randomUUID();
    private static final UUID IMOVEL_ID = UUID.randomUUID();

    @Test
    void deveAceitarDoisProprietariosComCinquentaPorCentoEUmPrincipal() {
        List<ImovelProprietario> proprietarios = List.of(
            proprietario(UUID.randomUUID(), new BigDecimal("50"), true),
            proprietario(UUID.randomUUID(), new BigDecimal("50"), false)
        );

        assertThatCode(() -> ImovelProprietario.validarComposicao(proprietarios))
            .doesNotThrowAnyException();
    }

    @Test
    void deveRejeitarSomaDiferenteDeCem() {
        List<ImovelProprietario> proprietarios = List.of(
            proprietario(UUID.randomUUID(), new BigDecimal("60"), true),
            proprietario(UUID.randomUUID(), new BigDecimal("50"), false)
        );

        assertThatThrownBy(() -> ImovelProprietario.validarComposicao(proprietarios))
            .isInstanceOf(RegraNegocioException.class)
            .hasMessageContaining("100%");
    }

    @Test
    void deveRejeitarQuandoNaoHaPrincipal() {
        List<ImovelProprietario> proprietarios = List.of(
            proprietario(UUID.randomUUID(), new BigDecimal("50"), false),
            proprietario(UUID.randomUUID(), new BigDecimal("50"), false)
        );

        assertThatThrownBy(() -> ImovelProprietario.validarComposicao(proprietarios))
            .isInstanceOf(RegraNegocioException.class)
            .hasMessageContaining("principal");
    }

    @Test
    void deveRejeitarQuandoHaDoisPrincipais() {
        List<ImovelProprietario> proprietarios = List.of(
            proprietario(UUID.randomUUID(), new BigDecimal("50"), true),
            proprietario(UUID.randomUUID(), new BigDecimal("50"), true)
        );

        assertThatThrownBy(() -> ImovelProprietario.validarComposicao(proprietarios))
            .isInstanceOf(RegraNegocioException.class)
            .hasMessageContaining("principal");
    }

    private static ImovelProprietario proprietario(UUID contribuinteId, BigDecimal porcentagem, boolean principal) {
        return new ImovelProprietario(
            UUID.randomUUID(),
            TENANT_ID,
            IMOVEL_ID,
            contribuinteId,
            porcentagem,
            principal
        );
    }
}
