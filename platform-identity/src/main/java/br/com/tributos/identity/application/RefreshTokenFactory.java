package br.com.tributos.identity.application;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

import br.com.tributos.identity.domain.RefreshToken;

/**
 * Gera o par (valor opaco para o cliente, hash para persistir) de um refresh token. Só o
 * hash SHA-256 é gravado no banco — {@link #hash} é usado tanto aqui (na emissão) quanto
 * na hora de procurar o token recebido do cliente (nunca se busca pelo valor em texto
 * puro). Não é {@code @Component}: é uma função pura, sem estado além do gerador
 * aleatório, então fábrica estática é suficiente e evita injeção desnecessária.
 */
public final class RefreshTokenFactory {

    private static final Duration DURACAO_REFRESH_TOKEN = Duration.ofDays(30);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private RefreshTokenFactory() {
    }

    public static Emitido emitir(UUID usuarioId, UUID tenantId) {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        String valorPlano = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        RefreshToken entidade = RefreshToken.novo(usuarioId, tenantId, hash(valorPlano), Instant.now().plus(DURACAO_REFRESH_TOKEN));
        return new Emitido(valorPlano, entidade);
    }

    public static String hash(String valorPlano) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(valorPlano.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 não disponível na JVM — ambiente inválido.", ex);
        }
    }

    public record Emitido(String valorPlano, RefreshToken entidade) {
    }
}
