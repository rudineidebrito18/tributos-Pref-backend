package br.com.tributos.itbi.adapters.in.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.tributos.itbi.domain.GuiaItbi;
import br.com.tributos.itbi.domain.SituacaoGuiaItbi;

public record GuiaItbiResponse(
    UUID id,
    long numero,
    UUID imovelId,
    UUID adquirenteId,
    UUID tipoGuiaId,
    UUID naturezaTransmissaoId,
    Instant dataSolicitacao,
    BigDecimal valorTransacao,
    BigDecimal valorVenalReferencia,
    BigDecimal baseCalculo,
    BigDecimal aliquota,
    BigDecimal valorItbi,
    SituacaoGuiaItbi situacao,
    boolean transferenciaTitularidadeRealizada
) {
    public static GuiaItbiResponse de(GuiaItbi g) {
        return new GuiaItbiResponse(
            g.id(), g.numero(), g.imovelId(), g.adquirenteId(), g.tipoGuiaId(), g.naturezaTransmissaoId(),
            g.dataSolicitacao(), g.valorTransacao(), g.valorVenalReferencia(), g.baseCalculo(), g.aliquota(),
            g.valorItbi(), g.situacao(), g.transferenciaTitularidadeRealizada()
        );
    }
}
