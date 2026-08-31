package br.com.tributos.iss.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import br.com.tributos.iss.domain.SituacaoFiscalAlvara;

public record EmitirAlvaraComando(
    UUID contribuinteId,
    UUID tipoAlvaraId,
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
