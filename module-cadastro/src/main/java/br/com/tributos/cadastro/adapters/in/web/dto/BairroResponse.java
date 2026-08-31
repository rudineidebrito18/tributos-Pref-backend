package br.com.tributos.cadastro.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.tributos.cadastro.domain.Bairro;

public record BairroResponse(
    UUID id,
    UUID cidadeId,
    String nome,
    UUID zonaFiscalId,
    BigDecimal valorTerreno
) {
    public static BairroResponse de(Bairro bairro) {
        return new BairroResponse(
            bairro.id(), bairro.cidadeId(), bairro.nome(), bairro.zonaFiscalId(), bairro.valorTerreno()
        );
    }
}
