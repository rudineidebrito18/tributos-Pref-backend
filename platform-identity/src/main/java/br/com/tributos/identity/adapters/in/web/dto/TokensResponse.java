package br.com.tributos.identity.adapters.in.web.dto;

import br.com.tributos.identity.application.TokensDeAcesso;

public record TokensResponse(String accessToken, String refreshToken, String tipoToken, long expiraEmSegundos) {

    public static TokensResponse de(TokensDeAcesso tokens) {
        return new TokensResponse(tokens.accessToken(), tokens.refreshToken(), tokens.tipoToken(), tokens.expiraEmSegundos());
    }
}
