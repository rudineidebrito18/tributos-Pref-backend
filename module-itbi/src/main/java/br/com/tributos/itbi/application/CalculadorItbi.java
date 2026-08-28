package br.com.tributos.itbi.application;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CalculadorItbi {

    private CalculadorItbi() {
    }

    public static BigDecimal calcularBase(BigDecimal valorTransacao, BigDecimal valorVenalReferencia) {
        return valorTransacao.max(valorVenalReferencia);
    }

    public static BigDecimal calcularValorItbi(BigDecimal baseCalculo, BigDecimal aliquota) {
        return baseCalculo.multiply(aliquota).setScale(2, RoundingMode.HALF_UP);
    }
}
