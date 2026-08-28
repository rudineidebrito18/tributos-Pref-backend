package br.com.tributos.kernel.exception;

/**
 * Falha de autenticação — credenciais inválidas, código MFA incorreto, ou token (access,
 * refresh ou de desafio MFA) inválido/expirado/revogado. Mapeada para HTTP 401 pelo
 * handler global. Mensagem é sempre genérica o suficiente para não revelar QUAL parte
 * falhou (ex.: nunca "usuário não existe" vs. "senha incorreta" — ambas viram "credenciais
 * inválidas"), para não facilitar enumeração de logins válidos.
 */
public class AutenticacaoException extends DomainException {

    public AutenticacaoException(String mensagem) {
        super(mensagem);
    }
}
