package br.com.tributos.iptu.application;

import java.math.BigDecimal;
import java.util.UUID;

public record SalvarImovelComando(
    String codigoLegado,
    UUID proprietarioId,
    UUID tipoId,
    UUID enderecoId,
    BigDecimal areaTerreno,
    BigDecimal areaConstruida,
    UUID destinacaoId,
    UUID tipoEdificacaoId,
    UUID tipoLimitacaoId,
    BigDecimal valorVenalTerreno,
    BigDecimal valorVenalConstrucao,
    SituacaoImovelComando situacao
) {
}
