package br.com.tributos.iptu.adapters.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import br.com.tributos.iptu.application.SituacaoImovelComando;

import jakarta.validation.constraints.NotNull;

public record SalvarImovelRequest(
    String codigoLegado,
    @NotNull(message = "Informe o proprietário.")
    UUID proprietarioId,
    @NotNull(message = "Informe o tipo do imóvel.")
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
    SituacaoImovelComando situacao,
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
