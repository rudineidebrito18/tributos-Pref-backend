package br.com.tributos.iss.adapters.in.web.dto;

import java.math.BigDecimal;
import java.time.Instant;

import br.com.tributos.iss.application.GerenciarTipoAlvaraService.HistoricoValorAlvaraItem;

public record HistoricoValorAlvaraResponse(
    Instant dataHoraAtualizacao,
    String usuario,
    BigDecimal valor,
    short anoVigencia
) {

    public static HistoricoValorAlvaraResponse de(HistoricoValorAlvaraItem item) {
        return new HistoricoValorAlvaraResponse(
            item.dataHoraAtualizacao(),
            item.usuario(),
            item.valor(),
            item.anoVigencia()
        );
    }
}
