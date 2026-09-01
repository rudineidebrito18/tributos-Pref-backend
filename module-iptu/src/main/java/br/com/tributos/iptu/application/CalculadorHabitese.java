package br.com.tributos.iptu.application;

import java.math.BigDecimal;

import br.com.tributos.iptu.domain.ImovelHabiteseTipo;
import br.com.tributos.kernel.exception.ValidationException;

public final class CalculadorHabitese {

    private CalculadorHabitese() {
    }

    public record ResultadoCalculo(BigDecimal baseCalculo, BigDecimal valor) {
    }

    public static ResultadoCalculo calcular(
        ImovelHabiteseTipo tipo,
        BigDecimal areaImovel,
        BigDecimal valorBaseCalculo,
        BigDecimal desconto
    ) {
        BigDecimal descontoEfetivo = desconto != null ? desconto : BigDecimal.ZERO;

        if (!tipo.permiteDesconto() && descontoEfetivo.compareTo(BigDecimal.ZERO) > 0) {
            throw new ValidationException("O tipo de habite-se selecionado não permite desconto.");
        }

        if (tipo.habilitaCalculoValor()) {
            if (areaImovel == null) {
                throw new ValidationException("Informe a área do imóvel para cálculo do habite-se.");
            }
            if (valorBaseCalculo == null) {
                throw new ValidationException("Informe o valor base de cálculo do habite-se.");
            }
            BigDecimal baseCalculo = areaImovel.multiply(valorBaseCalculo);
            return new ResultadoCalculo(baseCalculo, baseCalculo.subtract(descontoEfetivo));
        }

        return new ResultadoCalculo(null, tipo.valor());
    }
}
