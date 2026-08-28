package br.com.tributos.iss.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Valida o cálculo progressivo do Anexo III (Simples Nacional) para ISS,
 * usando as faixas oficiais vigentes em 2024.
 */
class CalculadorAliquotaSimplesNacionalTest {

    private static final List<CalculadorAliquotaSimplesNacional.FaixaAliquota> FAIXAS_ANEXO_III = List.of(
        faixa("0", "180000", "6.0000", "0", "33.5000", "III"),
        faixa("180000.01", "360000", "11.2000", "9360", "33.5000", "III"),
        faixa("360000.01", "720000", "13.5000", "17640", "33.5000", "III"),
        faixa("720000.01", "1800000", "16.0000", "35640", "33.5000", "III"),
        faixa("1800000.01", "3600000", "21.0000", "125640", "33.5000", "III"),
        faixa("3600000.01", "4800000", "33.0000", "648000", "33.5000", "III")
    );

    @ParameterizedTest
    @MethodSource("cenariosReceitaBruta")
    void deveCalcularAliquotaIssEfetivaPorFaixa(BigDecimal receitaBrutaAcumulada12Meses) {
        CalculadorAliquotaSimplesNacional.Resultado resultado =
            CalculadorAliquotaSimplesNacional.calcular(receitaBrutaAcumulada12Meses, FAIXAS_ANEXO_III);

        BigDecimal esperado = calcularAliquotaIssEsperada(receitaBrutaAcumulada12Meses, FAIXAS_ANEXO_III);

        assertThat(resultado.aliquotaIssEfetiva())
            .isCloseTo(esperado, within(new BigDecimal("0.000001")));
        assertThat(resultado.anexoSimples()).isEqualTo("III");
    }

    static Stream<Arguments> cenariosReceitaBruta() {
        return Stream.of(
            Arguments.of(new BigDecimal("100000")),
            Arguments.of(new BigDecimal("200000")),
            Arguments.of(new BigDecimal("500000")),
            Arguments.of(new BigDecimal("1000000")),
            Arguments.of(new BigDecimal("2000000")),
            Arguments.of(new BigDecimal("4000000"))
        );
    }

    private static CalculadorAliquotaSimplesNacional.FaixaAliquota faixa(
        String min,
        String max,
        String nominal,
        String parcela,
        String percentualIss,
        String anexo
    ) {
        return new CalculadorAliquotaSimplesNacional.FaixaAliquota(
            new BigDecimal(min),
            new BigDecimal(max),
            new BigDecimal(nominal),
            new BigDecimal(parcela),
            new BigDecimal(percentualIss),
            anexo
        );
    }

    /**
     * Replica a fórmula de {@link CalculadorAliquotaSimplesNacional} para obter o valor esperado no teste.
     */
    private static BigDecimal calcularAliquotaIssEsperada(
        BigDecimal receitaBrutaAcumulada12Meses,
        List<CalculadorAliquotaSimplesNacional.FaixaAliquota> faixas
    ) {
        CalculadorAliquotaSimplesNacional.FaixaAliquota faixa = faixas.stream()
            .filter(f -> contem(f, receitaBrutaAcumulada12Meses))
            .findFirst()
            .orElseThrow();

        RoundingMode arredondamento = RoundingMode.HALF_EVEN;
        BigDecimal rbt12 = receitaBrutaAcumulada12Meses;
        BigDecimal aliquotaNominal = faixa.aliquotaNominal().divide(BigDecimal.valueOf(100), 10, arredondamento);

        BigDecimal aliquotaEfetivaSimples = rbt12.multiply(aliquotaNominal)
            .subtract(faixa.parcelaDeduzir())
            .divide(rbt12, 10, arredondamento)
            .multiply(BigDecimal.valueOf(100))
            .setScale(6, arredondamento);

        BigDecimal percentualIss = faixa.percentualIss().divide(BigDecimal.valueOf(100), 10, arredondamento);
        return aliquotaEfetivaSimples.multiply(percentualIss).setScale(6, arredondamento);
    }

    private static boolean contem(CalculadorAliquotaSimplesNacional.FaixaAliquota faixa, BigDecimal receita) {
        boolean acimaMin = receita.compareTo(faixa.faixaReceitaMin()) >= 0;
        boolean abaixoMax = faixa.faixaReceitaMax() == null || receita.compareTo(faixa.faixaReceitaMax()) <= 0;
        return acimaMin && abaixoMax;
    }
}
