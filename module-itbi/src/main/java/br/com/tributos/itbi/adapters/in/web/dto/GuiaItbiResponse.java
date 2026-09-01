package br.com.tributos.itbi.adapters.in.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import br.com.tributos.itbi.domain.GuiaItbi;
import br.com.tributos.itbi.domain.SituacaoGuiaItbi;
import br.com.tributos.itbi.domain.TipoTributacaoItbi;

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
    boolean transferenciaTitularidadeRealizada,
    LocalDate dataTransacao,
    BigDecimal percentualTransmitido,
    BigDecimal valorNaoFinanciado,
    BigDecimal valorFinanciado,
    BigDecimal desconto,
    TipoTributacaoItbi tipoTributacao,
    String observacao,
    String motivoCancelamento,
    String codigoVerificacao
) {
    public static GuiaItbiResponse de(GuiaItbi g, UUID adquirentePessoaId) {
        return new GuiaItbiResponse(
            g.id(), g.numero(), g.imovelId(), adquirentePessoaId, g.tipoGuiaId(), g.naturezaTransmissaoId(),
            g.dataSolicitacao(), g.valorTransacao(), g.valorVenalReferencia(), g.baseCalculo(), g.aliquota(),
            g.valorItbi(), g.situacao(), g.transferenciaTitularidadeRealizada(),
            g.dataTransacao(), g.percentualTransmitido(), g.valorNaoFinanciado(), g.valorFinanciado(),
            g.desconto(), g.tipoTributacao(), g.observacao(), g.motivoCancelamento(), g.codigoVerificacao()
        );
    }
}
