package br.com.tributos.financeiro.adapters.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import br.com.tributos.financeiro.domain.TipoTributacao;
import br.com.tributos.financeiro.domain.TipoTributo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EmitirDamAvulsoRequest(
    @NotNull UUID contribuinteId,
    @NotNull @Positive BigDecimal valor,
    @NotNull LocalDate dataVencimento,
    @NotBlank String descricao,
    TipoTributo tipoTributo,
    TipoTributacao tipoTributacao
) {
}
