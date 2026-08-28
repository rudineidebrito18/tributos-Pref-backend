package br.com.tributos.adapters.out.financeiro;

import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.kernel.financeiro.PendenciaFinanceiraPort;

/**
 * Stub até o módulo Financeiro (Sprint 8) implementar a consulta real de pendências.
 */
@Component
public class PendenciaFinanceiraStubAdapter implements PendenciaFinanceiraPort {

    @Override
    public boolean possuiPendencia(UUID tenantId, UUID contribuinteId) {
        return false;
    }
}
