package br.com.tributos.iptu.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record Imovel(
    UUID id,
    UUID tenantId,
    long numeroCadastro,
    String codigoLegado,
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
    SituacaoImovel situacao,
    Short anoExercicio,
    LocalDate dataInclusao,
    BigDecimal areaTotal,
    BigDecimal frente,
    BigDecimal fundos,
    BigDecimal ladoEsquerdo,
    BigDecimal ladoDireito,
    String quadra,
    String lote,
    String loteamento,
    String edificio,
    String bloco,
    String sala,
    String apartamento,
    UUID bairroIptuId,
    UUID logradouroIptuId,
    BigDecimal valorVenalUnidade,
    BigDecimal valorAvaliacao,
    UUID enderecoCorrespondenciaId,
    String observacao
) {
}
