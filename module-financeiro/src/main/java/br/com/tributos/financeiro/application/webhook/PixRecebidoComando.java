package br.com.tributos.financeiro.application.webhook;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PixRecebidoComando(
    String endToEndId,
    String txid,
    BigDecimal valor,
    ComponentesValorComando componentesValor,
    String chave,
    OffsetDateTime horario,
    String infoPagador,
    PagadorComando pagador
) {
}
