package br.com.tributos.kernel.financeiro;

import java.util.UUID;

/**
 * Porta para consulta de pendências financeiras de um contribuinte — implementada pelo
 * módulo Financeiro (Sprint 8). Enquanto inexistente, app-bootstrap fornece stub.
 */
public interface PendenciaFinanceiraPort {

    /** {@code pessoaId} do cadastro único — titular da dívida na guia de arrecadação. */
    boolean possuiPendencia(UUID tenantId, UUID pessoaId);
}
