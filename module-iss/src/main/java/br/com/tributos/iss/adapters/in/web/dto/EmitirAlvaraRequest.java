package br.com.tributos.iss.adapters.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

import br.com.tributos.iss.domain.SituacaoFiscalAlvara;

public record EmitirAlvaraRequest(
    @NotNull(message = "Informe o contribuinte.")
    UUID contribuinteId,
    @NotNull(message = "Informe o tipo de alvará.")
    UUID tipoAlvaraId,
    @NotNull(message = "Informe a data de expedição.")
    LocalDate dataExpedicao,
    SituacaoFiscalAlvara situacaoFiscal,
    LocalDate validade,
    BigDecimal valorPorUnidade,
    String unidadeMedidaDescritivo,
    BigDecimal qtdUnidadeMedida,
    BigDecimal valor,
    String documentoHtml,
    String responsavelTecnico,
    String inscricaoConselhoRt,
    String observacao
) {
}
