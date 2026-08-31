package br.com.tributos.iss.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record EmitirNotaFiscalComando(
    UUID contribuinteId,
    UUID tomadorId,
    UUID servicoId,
    LocalDate competencia,
    BigDecimal valorServico,
    BigDecimal valorDeducoes,
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
