package br.com.tributos.identity.application;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Gera a senha temporária do primeiro usuário ADMIN_TENANT de um tenant recém-criado (ver
 * {@link CriarTenantService}) — devolvida em texto puro UMA única vez na resposta do
 * onboarding, nunca persistida (só o hash BCrypt vai para {@code usuario.senha_hash}).
 * Função pura, sem estado além do gerador aleatório — mesmo racional de
 * {@code RefreshTokenFactory} não ser {@code @Component}.
 */
public final class SenhaTemporariaFactory {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int TAMANHO_EM_BYTES = 16;

    private SenhaTemporariaFactory() {
    }

    public static String gerar() {
        byte[] bytes = new byte[TAMANHO_EM_BYTES];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
