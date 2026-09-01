package br.com.tributos.itbi.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record GuiaItbi(
    UUID id,
    UUID tenantId,
    long numero,
    UUID imovelId,
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
}
