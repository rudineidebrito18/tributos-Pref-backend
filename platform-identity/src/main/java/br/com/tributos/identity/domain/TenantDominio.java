package br.com.tributos.identity.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Domínio próprio de um tenant (ex. {@code tributos.prefeitura.gov.br}), alternativa ao
 * subdomínio padrão da plataforma — ver PLANEJAMENTO_PROJETO.md §8.1 (decisão
 * 2026-08-28). {@code verificado} só documenta intenção nesta fase: a verificação real de
 * propriedade (registro DNS TXT/CNAME) ainda não está automatizada, e a resolução de
 * tenant no {@code middleware.ts} do frontend ainda não consulta esta tabela — o cadastro
 * aqui é o primeiro passo, feito no onboarding administrativo do tenant.
 */
public record TenantDominio(UUID id, UUID tenantId, String dominio, boolean verificado, Instant criadoEm) {

    public static TenantDominio novo(UUID tenantId, String dominio) {
        return new TenantDominio(UUID.randomUUID(), tenantId, dominio, false, Instant.now());
    }
}
