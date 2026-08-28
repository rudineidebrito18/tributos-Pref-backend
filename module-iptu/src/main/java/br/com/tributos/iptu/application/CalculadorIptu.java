package br.com.tributos.iptu.application;

import java.math.BigDecimal;
import java.math.RoundingMode;

import br.com.tributos.iptu.domain.Imovel;

public final class CalculadorIptu {

    private CalculadorIptu() {
    }

    public static BigDecimal calcularValorVenal(Imovel imovel) {
        BigDecimal terreno = imovel.valorVenalTerreno() != null ? imovel.valorVenalTerreno() : BigDecimal.ZERO;
        BigDecimal construcao = imovel.valorVenalConstrucao() != null ? imovel.valorVenalConstrucao() : BigDecimal.ZERO;
        return terreno.add(construcao);
    }

    public static BigDecimal calcularValorIptu(BigDecimal valorVenal, BigDecimal aliquota) {
        return valorVenal.multiply(aliquota).setScale(2, RoundingMode.HALF_UP);
    }
}
