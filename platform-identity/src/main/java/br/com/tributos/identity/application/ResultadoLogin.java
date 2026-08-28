package br.com.tributos.identity.application;

/**
 * Resultado de {@code POST /api/auth/login}: ou o login já está completo (usuário sem
 * MFA), ou falta a segunda etapa. Modelado como {@code sealed interface} para o
 * controller ser obrigado (pelo compilador) a tratar os dois casos — não dá para esquecer
 * de checar "será que precisa de MFA?" como daria com um DTO com campo booleano solto.
 */
public sealed interface ResultadoLogin {

    record Autenticado(TokensDeAcesso tokens) implements ResultadoLogin {
    }

    record DesafioMfaNecessario(String tokenMfaPendente) implements ResultadoLogin {
    }
}
