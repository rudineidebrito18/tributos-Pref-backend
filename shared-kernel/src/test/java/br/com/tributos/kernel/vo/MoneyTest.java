package br.com.tributos.kernel.vo;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class MoneyTest {

    @Test
    void deveAplicarAliquotaPercentualComEscalaConsistente() {
        Money base = Money.of("1000.00");

        Money iss = base.aplicarAliquotaPercentual(BigDecimal.valueOf(5));

        assertThat(iss).isEqualTo(Money.of("50.00"));
    }

    @Test
    void doisValoresComEscalasDiferentesDevemSerIguaisSeRepresentamOMesmoMontante() {
        assertThat(Money.of("10")).isEqualTo(Money.of("10.00"));
    }

    @Test
    void somaEDeveRespeitarComparacaoDeMaiorQue() {
        Money total = Money.of("100.00").somar(Money.of("50.00"));

        assertThat(total.maiorQue(Money.of("149.99"))).isTrue();
        assertThat(total).isEqualTo(Money.of("150.00"));
    }
}
