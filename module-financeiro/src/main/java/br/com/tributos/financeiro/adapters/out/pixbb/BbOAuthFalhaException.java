package br.com.tributos.financeiro.adapters.out.pixbb;

public class BbOAuthFalhaException extends RuntimeException {

    public BbOAuthFalhaException(String mensagem) {
        super(mensagem);
    }

    public BbOAuthFalhaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
