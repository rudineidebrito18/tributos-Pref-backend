package br.com.tributos.iptu.adapters.in.web.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import br.com.tributos.iptu.domain.HabiteseImovel;

public record HabiteseImovelResponse(
    UUID id,
    UUID imovelId,
    UUID tipoId,
    long numero,
    LocalDate dataEmissao,
    Instant dataEmissaoTs
) {

    public static HabiteseImovelResponse de(HabiteseImovel habitese) {
        return new HabiteseImovelResponse(
            habitese.id(),
            habitese.imovelId(),
            habitese.tipoId(),
            habitese.numero(),
            habitese.dataEmissao(),
            habitese.dataEmissaoTs()
        );
    }
}
