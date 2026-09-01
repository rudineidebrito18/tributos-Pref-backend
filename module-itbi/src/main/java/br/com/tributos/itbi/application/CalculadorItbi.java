package br.com.tributos.itbi.application;

import java.math.BigDecimal;
import java.math.RoundingMode;

import br.com.tributos.itbi.domain.TipoTributacaoItbi;

public final class CalculadorItbi {

    private static final BigDecimal CEM = new BigDecimal("100");

    private CalculadorItbi() {
    }

    public static BigDecimal calcularBase(
        BigDecimal valorTransacao,
        BigDecimal valorVenalReferencia,
        BigDecimal percentualTransmitido
    ) {
        BigDecimal maiorValor = valorTransacao.max(valorVenalReferencia);
        BigDecimal percentual = percentualTransmitido != null ? percentualTransmitido : CEM;
        return maiorValor.multiply(percentual)
            .divide(CEM, 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calcularValorItbi(
        BigDecimal baseCalculo,
        BigDecimal aliquota,
        BigDecimal desconto,
        TipoTributacaoItbi tipoTributacao
    ) {
        if (tipoTributacao != null && tipoTributacao != TipoTributacaoItbi.TRIBUTAVEL) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal descontoAplicado = desconto != null ? desconto : BigDecimal.ZERO;
        return baseCalculo.multiply(aliquota)
            .setScale(2, RoundingMode.HALF_UP)
            .subtract(descontoAplicado)
            .max(BigDecimal.ZERO)
            .setScale(2, RoundingMode.HALF_UP);
    }

    /** @deprecated use {@link #calcularBase(BigDecimal, BigDecimal, BigDecimal)} */
    @Deprecated
    public static BigDecimal calcularBase(BigDecimal valorTransacao, BigDecimal valorVenalReferencia) {
        return calcularBase(valorTransacao, valorVenalReferencia, CEM);
    }

    /** @deprecated use {@link #calcularValorItbi(BigDecimal, BigDecimal, BigDecimal, TipoTributacaoItbi)} */
    @Deprecated
    public static BigDecimal calcularValorItbi(BigDecimal baseCalculo, BigDecimal aliquota) {
        return calcularValorItbi(baseCalculo, aliquota, BigDecimal.ZERO, TipoTributacaoItbi.TRIBUTAVEL);
    }
}
