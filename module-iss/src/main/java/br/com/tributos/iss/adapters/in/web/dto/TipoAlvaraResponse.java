package br.com.tributos.iss.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.tributos.iss.domain.TipoAlvara;

public record TipoAlvaraResponse(UUID id, String nome, BigDecimal valorBase, int diasValidade, boolean ativo) {

    public static TipoAlvaraResponse de(TipoAlvara tipo) {
        return new TipoAlvaraResponse(
            tipo.id(), tipo.nome(), tipo.valorBase(), tipo.diasValidade(), tipo.ativo()
        );
    }
}
