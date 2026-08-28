package br.com.tributos.iss.adapters.in.web.dto;

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
    @NotNull(message = "Informe a situação fiscal.")
    SituacaoFiscalAlvara situacaoFiscal,
    LocalDate validade
) {
}
