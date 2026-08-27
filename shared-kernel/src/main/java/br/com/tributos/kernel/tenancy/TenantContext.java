package br.com.tributos.kernel.tenancy;

import java.util.UUID;

/**
 * Guarda o tenant da requisição corrente. Preenchido pelo filtro de resolução de tenant
 * (camada de infraestrutura, em app-bootstrap) e lido por qualquer camada que precise do
 * tenant atual sem recebê-lo explicitamente por parâmetro — em especial pelo interceptor
 * que executa {@code SET LOCAL app.current_tenant} na conexão JDBC (ver
 * PLANEJAMENTO_PROJETO.md §6.2), para que o RLS do PostgreSQL enxergue o tenant certo.
 *
 * <p>{@code ThreadLocal} de propósito: cada requisição HTTP é processada numa thread
 * dedicada no modelo servlet tradicional. {@link #clear()} é chamado no {@code finally} do
 * filtro — nunca deixar o valor vazar para a próxima requisição que reaproveitar a thread
 * (pool de threads do servidor) é o requisito de segurança mais importante desta classe.
 */
public final class TenantContext {

    private static final ThreadLocal<UUID> TENANT_ATUAL = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void set(UUID tenantId) {
        TENANT_ATUAL.set(tenantId);
    }

    public static UUID get() {
        return TENANT_ATUAL.get();
    }

    /** Lança {@link IllegalStateException} se nenhum tenant foi resolvido para a requisição atual. */
    public static UUID getObrigatorio() {
        UUID tenantId = TENANT_ATUAL.get();
        if (tenantId == null) {
            throw new IllegalStateException(
                "Nenhum tenant resolvido para a requisição atual — TenantResolverFilter não executou "
                    + "ou este código está rodando fora do ciclo de uma requisição HTTP.");
        }
        return tenantId;
    }

    public static void clear() {
        TENANT_ATUAL.remove();
    }
}
