package br.com.tributos.iptu.adapters.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record EmitirHabiteseRequest(
    @NotNull(message = "Informe o tipo do habite-se.")
    UUID tipoId,
    @NotNull(message = "Informe a data de emissão.")
    LocalDate dataEmissao,
    @NotNull(message = "Informe o ano do habite-se.")
    Short ano,
    LocalDate validade,
    @NotNull(message = "Informe o contribuinte.")
    UUID contribuinteId,
    @NotNull(message = "Informe a área do imóvel.")
    BigDecimal areaImovel,
    @NotNull(message = "Informe a data de conclusão da obra.")
    LocalDate dataConclusao,
    String numeroAlvara,
    LocalDate dataAlvara,
    LocalDate validadeAlvara,
    @NotNull(message = "Informe o valor base de cálculo.")
    BigDecimal valorBaseCalculo,
    BigDecimal desconto,
    BigDecimal frente,
    BigDecimal fundos,
    BigDecimal ladoEsquerdo,
    BigDecimal ladoDireito,
    String observacao,
    @Valid
    List<HabiteseResponsavelRequest> responsaveis
) {
}
