package br.com.tributos.itbi.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.tributos.itbi.domain.NaturezaTransmissao;
import br.com.tributos.itbi.domain.TipoGuiaItbi;

public record TipoGuiaItbiResponse(UUID id, String nome, BigDecimal aliquota) {
    public static TipoGuiaItbiResponse de(TipoGuiaItbi t) {
        return new TipoGuiaItbiResponse(t.id(), t.nome(), t.aliquota());
    }
}
