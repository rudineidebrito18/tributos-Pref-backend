package br.com.tributos.kernel.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Publicado pelo módulo IPTU após emissão de habite-se com valor — consumido pelo Financeiro.
 */
public record HabiteseEmitidoEvent(
    UUID habiteseId,
    UUID tenantId,
    UUID contribuintePessoaId,
    BigDecimal valor,
    Instant dataEmissao
) {
}
