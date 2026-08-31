package br.com.tributos.iss.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.tributos.kernel.exception.RegraNegocioException;

class AtividadeServicoTest {

    @Test
    void deveRejeitarImuneComAliquotaDiferenteDeZero() {
        assertThatThrownBy(() -> new AtividadeServico(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            new BigDecimal("5.000000"),
            true,
            true,
            false,
            false,
            false,
            null,
            null
        )).isInstanceOf(RegraNegocioException.class)
            .hasMessageContaining("alíquota zero");
    }
}
