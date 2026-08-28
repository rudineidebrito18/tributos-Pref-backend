package br.com.tributos.kernel.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Publicado pelo módulo ISS após emissão de NFS-e — consumido pelo Financeiro para gerar guia.
 */
public record NotaFiscalEmitidaEvent(
    UUID notaId,
    UUID tenantId,
    UUID contribuintePessoaId,
    BigDecimal valorIss,
    LocalDate competencia,
    Instant dataEmissao
) {
}
