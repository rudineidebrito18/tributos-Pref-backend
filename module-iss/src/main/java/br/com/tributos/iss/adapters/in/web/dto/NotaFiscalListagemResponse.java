package br.com.tributos.iss.adapters.in.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.tributos.iss.application.ListarNotasFiscaisService.NotaFiscalListagemItem;

public record NotaFiscalListagemResponse(
    UUID id,
    long numero,
    String situacao,
    String contribuinte,
    Instant dataEmissao,
    BigDecimal valor,
    BigDecimal valorIss
) {

    public static NotaFiscalListagemResponse de(NotaFiscalListagemItem item) {
        return new NotaFiscalListagemResponse(
            item.id(),
            item.numero(),
            item.situacao(),
            item.contribuinte(),
            item.dataEmissao(),
            item.valor(),
            item.valorIss()
        );
    }
}
