package br.com.tributos.itbi.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import br.com.tributos.itbi.domain.TipoTributacaoItbi;

class CalculadorItbiTest {

    @Test
    void deveCalcularValorItbiConformeCasoDaAuditoria() {
        BigDecimal valorTransacao = new BigDecimal("171153.57");
        BigDecimal valorVenal = BigDecimal.ZERO;
        BigDecimal percentual = new BigDecimal("100");
        BigDecimal aliquota = new BigDecimal("0.02");

        BigDecimal base = CalculadorItbi.calcularBase(valorTransacao, valorVenal, percentual);
        BigDecimal valorItbi = CalculadorItbi.calcularValorItbi(base, aliquota, BigDecimal.ZERO, TipoTributacaoItbi.TRIBUTAVEL);

        assertThat(base).isEqualByComparingTo("171153.57");
        assertThat(valorItbi).isEqualByComparingTo("3423.07");
    }

    @Test
    void deveZerarValorItbiQuandoNaoTributavel() {
        BigDecimal base = new BigDecimal("171153.57");
        BigDecimal aliquota = new BigDecimal("0.02");

        BigDecimal valorItbi = CalculadorItbi.calcularValorItbi(base, aliquota, BigDecimal.ZERO, TipoTributacaoItbi.ISENTO);

        assertThat(valorItbi).isEqualByComparingTo("0.00");
    }
}
