package br.com.tributos.kernel.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Publicado pelo módulo ITBI após solicitação de guia — consumido pelo Financeiro.
 */
public record GuiaItbiSolicitadaEvent(
    UUID guiaItbiId,
    UUID tenantId,
    UUID adquirentePessoaId,
    BigDecimal valorItbi,
    Instant dataSolicitacao
) {
}
