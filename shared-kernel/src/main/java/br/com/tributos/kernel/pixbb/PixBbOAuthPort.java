package br.com.tributos.kernel.pixbb;

/**
 * Porta de autenticação OAuth client_credentials na API PIX Arrecadação do Banco do Brasil.
 */
public interface PixBbOAuthPort {

    ResultadoTokenPixBb obterToken(CredenciaisPixBb credenciais);

    ResultadoTesteConexaoPixBb testarConexao(CredenciaisPixBb credenciais);
}
