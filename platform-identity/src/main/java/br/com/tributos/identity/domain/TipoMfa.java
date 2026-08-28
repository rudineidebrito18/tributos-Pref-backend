package br.com.tributos.identity.domain;

/**
 * Fator de segunda etapa exigido no login. {@code EMAIL} está previsto no roadmap
 * (Sprint 0) mas ainda não implementado — só {@code TOTP} (Google Authenticator e
 * compatíveis) está funcional nesta fase.
 */
public enum TipoMfa {
    NENHUM,
    TOTP,
    EMAIL
}
