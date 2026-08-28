package br.com.tributos.kernel.financeiro;

import java.util.UUID;

/**
 * Porta para consulta de pendências financeiras de um contribuinte — implementada pelo
 * módulo Financeiro (Sprint 8). Enquanto inexistente, app-bootstrap fornece stub.
 */
public interface PendenciaFinanceiraPort {

    boolean possuiPendencia(UUID tenantId, UUID contribuinteId);
}
