package br.com.tributos.iss.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

/**
 * Calcula a alíquota efetiva de ISS para contribuintes do Simples Nacional (Anexo III),
 * corrigindo o bug do legado que aplicava alíquota fixa independente da faixa de receita.
 *
 * Fórmula: alíquota efetiva Simples = (RBT12 × alíquota nominal − parcela a deduzir) / RBT12
 * Alíquota ISS = alíquota efetiva Simples × percentual ISS na partilha (33,5% no Anexo III).
 */
public final class CalculadorAliquotaSimplesNacional {

    private static final int ESCALA_PERCENTUAL = 6;
    private static final RoundingMode ARREDONDAMENTO = RoundingMode.HALF_EVEN;

    private CalculadorAliquotaSimplesNacional() {
    }

    public static Resultado calcular(BigDecimal receitaBrutaAcumulada12Meses, List<FaixaAliquota> faixas) {
        Objects.requireNonNull(receitaBrutaAcumulada12Meses, "receitaBrutaAcumulada12Meses");
        Objects.requireNonNull(faixas, "faixas");

        if (receitaBrutaAcumulada12Meses.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("A receita bruta acumulada deve ser maior que zero.");
        }
        if (faixas.isEmpty()) {
            throw new IllegalArgumentException("Nenhuma faixa de alíquota configurada para o regime.");
        }

        FaixaAliquota faixa = faixas.stream()
            .filter(f -> f.contem(receitaBrutaAcumulada12Meses))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "Receita acumulada fora das faixas configuradas para o regime."));

        BigDecimal rbt12 = receitaBrutaAcumulada12Meses;
        BigDecimal aliquotaNominal = faixa.aliquotaNominal().divide(BigDecimal.valueOf(100), 10, ARREDONDAMENTO);
        BigDecimal parcela = faixa.parcelaDeduzir();

        BigDecimal aliquotaEfetivaSimples = rbt12.multiply(aliquotaNominal)
            .subtract(parcela)
            .divide(rbt12, 10, ARREDONDAMENTO)
            .multiply(BigDecimal.valueOf(100))
            .setScale(ESCALA_PERCENTUAL, ARREDONDAMENTO);

        BigDecimal percentualIss = faixa.percentualIss().divide(BigDecimal.valueOf(100), 10, ARREDONDAMENTO);
        BigDecimal aliquotaIssEfetiva = aliquotaEfetivaSimples
            .multiply(percentualIss)
            .setScale(ESCALA_PERCENTUAL, ARREDONDAMENTO);

        return new Resultado(
            faixa.anexoSimples(),
            faixa.faixaReceitaMin(),
            faixa.faixaReceitaMax(),
            faixa.aliquotaNominal(),
            aliquotaEfetivaSimples,
            aliquotaIssEfetiva
        );
    }

    public record FaixaAliquota(
        BigDecimal faixaReceitaMin,
        BigDecimal faixaReceitaMax,
        BigDecimal aliquotaNominal,
        BigDecimal parcelaDeduzir,
        BigDecimal percentualIss,
        String anexoSimples
    ) {
        boolean contem(BigDecimal receita) {
            boolean acimaMin = receita.compareTo(faixaReceitaMin) >= 0;
            boolean abaixoMax = faixaReceitaMax == null || receita.compareTo(faixaReceitaMax) <= 0;
            return acimaMin && abaixoMax;
        }
    }

    public record Resultado(
        String anexoSimples,
        BigDecimal faixaReceitaMin,
        BigDecimal faixaReceitaMax,
        BigDecimal aliquotaNominal,
        BigDecimal aliquotaEfetivaSimples,
        BigDecimal aliquotaIssEfetiva
    ) {
    }
}
