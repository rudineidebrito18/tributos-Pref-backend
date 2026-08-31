package br.com.tributos.iss.adapters.in.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import br.com.tributos.iss.domain.NotaFiscal;
import br.com.tributos.iss.domain.StatusNotaFiscal;

public record NotaFiscalResponse(
    UUID id,
    long numero,
    String serie,
    UUID contribuinteId,
    UUID tomadorId,
    UUID servicoId,
    LocalDate competencia,
    BigDecimal valorServico,
    BigDecimal valorDeducoes,
    BigDecimal baseCalculo,
    BigDecimal aliquotaAplicada,
    BigDecimal valorIss,
    BigDecimal valorIr,
    BigDecimal valorPis,
    BigDecimal valorCofins,
    BigDecimal valorCsll,
    BigDecimal valorInss,
    boolean issRetidoFonte,
    StatusNotaFiscal status,
    UUID notaSubstitutaId,
    String motivoCancelamento,
    Instant dataEmissao
) {

    public static NotaFiscalResponse de(NotaFiscal nota) {
        return new NotaFiscalResponse(
            nota.id(),
            nota.numero(),
            nota.serie(),
            nota.contribuinteId(),
            nota.tomadorId(),
            nota.servicoId(),
            nota.competencia(),
            nota.valorServico(),
            nota.valorDeducoes(),
            nota.baseCalculo(),
            nota.aliquotaAplicada(),
            nota.valorIss(),
            nota.valorIr(),
            nota.valorPis(),
            nota.valorCofins(),
            nota.valorCsll(),
            nota.valorInss(),
            nota.issRetidoFonte(),
            nota.status(),
            nota.notaSubstitutaId(),
            nota.motivoCancelamento(),
            nota.dataEmissao()
        );
    }
}
