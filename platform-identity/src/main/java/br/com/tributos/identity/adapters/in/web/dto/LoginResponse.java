package br.com.tributos.identity.adapters.in.web.dto;

import br.com.tributos.identity.application.ResultadoLogin;
import br.com.tributos.identity.application.TokensDeAcesso;

/**
 * Formato único de resposta para {@code POST /api/auth/login}, para o frontend não
 * precisar inspecionar o status HTTP para saber se o fluxo terminou ou precisa de MFA:
 * {@code mfaNecessario=true} → chame {@code /api/auth/mfa/verificar} com
 * {@code tokenMfaPendente}; caso contrário, {@code tokens} já vem preenchido.
 */
public record LoginResponse(boolean mfaNecessario, String tokenMfaPendente, TokensResponse tokens) {

    public static LoginResponse de(ResultadoLogin resultado) {
        return switch (resultado) {
            case ResultadoLogin.Autenticado autenticado -> autenticado(autenticado.tokens());
            case ResultadoLogin.DesafioMfaNecessario desafio -> desafioMfa(desafio.tokenMfaPendente());
        };
    }

    private static LoginResponse autenticado(TokensDeAcesso tokens) {
        return new LoginResponse(false, null, TokensResponse.de(tokens));
    }

    private static LoginResponse desafioMfa(String tokenMfaPendente) {
        return new LoginResponse(true, tokenMfaPendente, null);
    }
}
