package br.com.tributos.adapters.in.web.dto.pixbb;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PixRecebido(
    String endToEndId,
    String txid,
    BigDecimal valor,
    ComponentesValor componentesValor,
    String chave,
    OffsetDateTime horario,
    String infoPagador,
    Pagador pagador
) {
}
