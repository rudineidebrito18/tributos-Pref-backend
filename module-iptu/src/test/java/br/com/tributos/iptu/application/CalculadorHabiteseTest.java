package br.com.tributos.iptu.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import br.com.tributos.iptu.domain.ImovelHabiteseTipo;
import br.com.tributos.kernel.exception.ValidationException;

class CalculadorHabiteseTest {

    @Test
    void deveCalcularValorPorArea() {
        ImovelHabiteseTipo tipo = tipo(true, true, new BigDecimal("100.00"));

        CalculadorHabitese.ResultadoCalculo resultado = CalculadorHabitese.calcular(
            tipo,
            new BigDecimal("120.5"),
            new BigDecimal("10.00"),
            new BigDecimal("50.00")
        );

        assertThat(resultado.baseCalculo()).isEqualByComparingTo("1205.00");
        assertThat(resultado.valor()).isEqualByComparingTo("1155.00");
    }

    @Test
    void deveUsarValorFixoDoTipo() {
        ImovelHabiteseTipo tipo = tipo(false, false, new BigDecimal("500.00"));

        CalculadorHabitese.ResultadoCalculo resultado = CalculadorHabitese.calcular(
            tipo,
            new BigDecimal("120.5"),
            new BigDecimal("10.00"),
            BigDecimal.ZERO
        );

        assertThat(resultado.baseCalculo()).isNull();
        assertThat(resultado.valor()).isEqualByComparingTo("500.00");
    }

    @Test
    void deveRejeitarDescontoQuandoTipoNaoPermite() {
        ImovelHabiteseTipo tipo = tipo(true, false, new BigDecimal("100.00"));

        assertThatThrownBy(() -> CalculadorHabitese.calcular(
            tipo,
            new BigDecimal("100"),
            new BigDecimal("10.00"),
            new BigDecimal("1.00")
        ))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("não permite desconto");
    }

    private static ImovelHabiteseTipo tipo(
        boolean habilitaCalculoValor,
        boolean permiteDesconto,
        BigDecimal valor
    ) {
        return new ImovelHabiteseTipo(
            null,
            null,
            "DEFINITIVO",
            true,
            null,
            permiteDesconto,
            habilitaCalculoValor,
            valor,
            null,
            null,
            null,
            null
        );
    }
}
