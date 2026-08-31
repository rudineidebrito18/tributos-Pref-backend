package br.com.tributos.itbi.adapters.in.web.dto;

import java.util.UUID;

import br.com.tributos.itbi.domain.TipoCalculoGuiaItbi;

public record TipoCalculoGuiaItbiResponse(UUID id, String descricao) {
    public static TipoCalculoGuiaItbiResponse de(TipoCalculoGuiaItbi tipoCalculo) {
        return new TipoCalculoGuiaItbiResponse(tipoCalculo.id(), tipoCalculo.descricao());
    }
}
