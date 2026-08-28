package br.com.tributos.iptu.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.tributos.iptu.domain.AliquotaIptu;

public record AliquotaIptuResponse(
    UUID id,
    int exercicio,
    UUID destinacaoId,
    UUID zonaFiscalId,
    BigDecimal aliquota
) {

    public static AliquotaIptuResponse de(AliquotaIptu aliquota) {
        return new AliquotaIptuResponse(
            aliquota.id(),
            aliquota.exercicio(),
            aliquota.destinacaoId(),
            aliquota.zonaFiscalId(),
            aliquota.aliquota()
        );
    }
}
