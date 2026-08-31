package br.com.tributos.financeiro.adapters.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.tributos.financeiro.domain.GuiaArrecadacao;
import br.com.tributos.financeiro.domain.StatusPix;

public record GerarPixResponse(
    String txid,
    String qrCodePayload,
    String pixLink,
    StatusPix statusPix,
    BigDecimal valor,
    LocalDate vencimento
) {
    public static GerarPixResponse de(GuiaArrecadacao guia) {
        return new GerarPixResponse(
            guia.pixTxid(),
            guia.pixQrcodePayload(),
            guia.pixLink(),
            guia.statusPix(),
            guia.valor(),
            guia.dataVencimento()
        );
    }
}
