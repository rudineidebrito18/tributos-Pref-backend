package br.com.tributos.iptu.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.tributos.iptu.domain.ValorTerrenoM2;

public record ValorTerrenoM2Response(
    UUID id,
    UUID zonaFiscalId,
    int exercicio,
    BigDecimal valorM2
) {

    public static ValorTerrenoM2Response de(ValorTerrenoM2 valor) {
        return new ValorTerrenoM2Response(valor.id(), valor.zonaFiscalId(), valor.exercicio(), valor.valorM2());
    }
}
