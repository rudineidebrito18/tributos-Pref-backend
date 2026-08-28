package br.com.tributos.kernel.financeiro;

import java.util.UUID;

/**
 * Porta para consulta de situação de guias de arrecadação — implementada pelo módulo Financeiro.
 */
public interface GuiaFinanceiraConsultaPort {

    /** {@code origemTipo} corresponde a {@code OrigemGuia.name()} do Financeiro (ex.: ITBI_GUIA). */
    boolean origemEstaPaga(UUID tenantId, String origemTipo, UUID origemId);
}
