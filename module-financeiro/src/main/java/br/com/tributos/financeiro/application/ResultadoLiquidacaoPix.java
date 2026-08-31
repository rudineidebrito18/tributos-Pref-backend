package br.com.tributos.financeiro.application;

import java.math.BigDecimal;
import java.time.Instant;

import br.com.tributos.financeiro.domain.GuiaArrecadacao;

public record ResultadoLiquidacaoPix(
    GuiaArrecadacao guia,
    boolean valorCompleto,
    boolean idempotente
) {
}
