package br.com.tributos.iss.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class CalculadorValorIss {

    private static final int ESCALA_MONETARIA = 2;
    private static final RoundingMode ARREDONDAMENTO = RoundingMode.HALF_EVEN;

    private CalculadorValorIss() {
    }

    public static Resultado calcular(BigDecimal valorServico, BigDecimal valorDeducoes, BigDecimal aliquotaPercent) {
        Objects.requireNonNull(valorServico, "valorServico");
        Objects.requireNonNull(valorDeducoes, "valorDeducoes");
        Objects.requireNonNull(aliquotaPercent, "aliquotaPercent");

        BigDecimal base = valorServico.subtract(valorDeducoes);
        if (base.compareTo(BigDecimal.ZERO) < 0) {
            base = BigDecimal.ZERO;
        }
        base = base.setScale(ESCALA_MONETARIA, ARREDONDAMENTO);

        BigDecimal aliquota = aliquotaPercent.divide(BigDecimal.valueOf(100), 10, ARREDONDAMENTO);
        BigDecimal valorIss = base.multiply(aliquota).setScale(ESCALA_MONETARIA, ARREDONDAMENTO);

        return new Resultado(base, valorIss);
    }

    public record Resultado(BigDecimal baseCalculo, BigDecimal valorIss) {
    }
}
