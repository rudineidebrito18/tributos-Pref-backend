package br.com.tributos.kernel.financeiro;

import java.util.UUID;

/**
 * Porta para consulta de pendências financeiras de um contribuinte — implementada pelo
 * módulo Financeiro (Sprint 8). Enquanto inexistente, app-bootstrap fornece stub.
 */
public interface PendenciaFinanceiraPort {

    /** {@code pessoaId} do cadastro único — titular da dívida na guia de arrecadação. */
    boolean possuiPendencia(UUID tenantId, UUID pessoaId);

    /**
     * Verifica guia {@code PENDENTE} para um tributo da certidão.
     *
     * @param codigoTributo código do enum {@code TributoCertidao} (ex.: {@code ISS}, {@code IPTU})
     */
    boolean possuiPendenciaTributo(UUID tenantId, UUID pessoaId, String codigoTributo);
}
