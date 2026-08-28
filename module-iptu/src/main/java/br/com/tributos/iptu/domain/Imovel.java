package br.com.tributos.iptu.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record Imovel(
    UUID id,
    UUID tenantId,
    long numeroCadastro,
    String codigoLegado,
    UUID proprietarioId,
    UUID tipoId,
    UUID enderecoId,
    BigDecimal areaTerreno,
    BigDecimal areaConstruida,
    UUID destinacaoId,
    UUID tipoEdificacaoId,
    UUID tipoLimitacaoId,
    UUID zonaFiscalId,
    BigDecimal valorVenalTerreno,
    BigDecimal valorVenalConstrucao,
    SituacaoImovel situacao
) {
}
