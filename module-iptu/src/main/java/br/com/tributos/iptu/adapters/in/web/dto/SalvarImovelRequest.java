package br.com.tributos.iptu.adapters.in.web.dto;

import java.math.BigDecimal;
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
    BigDecimal valorVenalTerreno,
    BigDecimal valorVenalConstrucao,
    SituacaoImovelComando situacao
) {
}
