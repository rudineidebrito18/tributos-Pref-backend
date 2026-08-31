package br.com.tributos.iss.adapters.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SubstituirNotaFiscalRequest(
    @NotNull(message = "Informe o tomador do serviço.")
    UUID tomadorId,
    @NotNull(message = "Informe o serviço prestado.")
    UUID servicoId,
    @NotNull(message = "Informe a competência da nota fiscal.")
    LocalDate competencia,
    @NotNull(message = "Informe o valor do serviço.")
    @Positive(message = "O valor do serviço deve ser maior que zero.")
    BigDecimal valorServico,
    BigDecimal valorDeducoes,
    @NotNull(message = "Informe a receita bruta acumulada dos últimos 12 meses.")
    @Positive(message = "A receita bruta acumulada deve ser maior que zero.")
    BigDecimal receitaBrutaAcumulada12Meses,
    String serie,
    UUID atividadeId,
    BigDecimal valorIr,
    BigDecimal valorPis,
    BigDecimal valorCofins,
    BigDecimal valorCsll,
    BigDecimal valorInss,
    Boolean issRetidoFonte
) {
}
