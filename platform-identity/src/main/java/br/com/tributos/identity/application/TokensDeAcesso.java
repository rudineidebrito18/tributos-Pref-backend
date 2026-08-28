package br.com.tributos.identity.application;

/** Par de tokens devolvido ao final de um login (ou refresh) bem-sucedido. */
public record TokensDeAcesso(String accessToken, String refreshToken, String tipoToken, long expiraEmSegundos) {

    public static TokensDeAcesso de(String accessToken, String refreshToken, long expiraEmSegundos) {
        return new TokensDeAcesso(accessToken, refreshToken, "Bearer", expiraEmSegundos);
    }
}
