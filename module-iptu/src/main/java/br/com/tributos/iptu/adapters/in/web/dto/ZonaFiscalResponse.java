package br.com.tributos.iptu.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.tributos.iptu.domain.ZonaFiscal;

public record ZonaFiscalResponse(
    UUID id,
    String nome,
    BigDecimal fatorValorizacao,
    boolean ativo
) {

    public static ZonaFiscalResponse de(ZonaFiscal zona) {
        return new ZonaFiscalResponse(zona.id(), zona.nome(), zona.fatorValorizacao(), zona.ativo());
    }
}
