package br.com.tributos.kernel.vo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object monetário. Existe para impedir a classe de bug mais comum em sistema
 * tributário — {@code BigDecimal} com escala inconsistente somado/comparado sem
 * normalização — centralizando a regra (escala 2, {@code HALF_EVEN}) num único lugar em
 * vez de espalhada em cada cálculo de ISS/IPTU/ITBI.
 */
public final class Money {

    private static final int ESCALA = 2;

    public static final Money ZERO = of(BigDecimal.ZERO);

    private final BigDecimal valor;

    private Money(BigDecimal valor) {
        this.valor = valor.setScale(ESCALA, RoundingMode.HALF_EVEN);
    }

    public static Money of(BigDecimal valor) {
        Objects.requireNonNull(valor, "valor não pode ser nulo");
        return new Money(valor);
    }

    public static Money of(String valor) {
        return of(new BigDecimal(valor));
    }

    public Money somar(Money outro) {
        return of(this.valor.add(outro.valor));
    }

    public Money subtrair(Money outro) {
        return of(this.valor.subtract(outro.valor));
    }

    public Money multiplicar(BigDecimal fator) {
        return of(this.valor.multiply(fator));
    }

    /** Aplica uma alíquota percentual (ex.: 2% → passe {@code 2}, não {@code 0.02}). */
    public Money aplicarAliquotaPercentual(BigDecimal aliquotaPercentual) {
        return of(this.valor.multiply(aliquotaPercentual).divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_EVEN));
    }

    public boolean maiorQue(Money outro) {
        return this.valor.compareTo(outro.valor) > 0;
    }

    public boolean isZero() {
        return this.valor.compareTo(BigDecimal.ZERO) == 0;
    }

    public BigDecimal valor() {
        return valor;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) return true;
        if (!(outro instanceof Money money)) return false;
        return valor.compareTo(money.valor) == 0;
    }

    @Override
    public int hashCode() {
        return valor.stripTrailingZeros().hashCode();
    }

    @Override
    public String toString() {
        return valor.toPlainString();
    }
}
