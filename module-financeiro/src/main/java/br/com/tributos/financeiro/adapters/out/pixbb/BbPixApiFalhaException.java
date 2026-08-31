package br.com.tributos.financeiro.adapters.out.pixbb;

public class BbPixApiFalhaException extends RuntimeException {

    public BbPixApiFalhaException(String mensagem) {
        super(mensagem);
    }

    public BbPixApiFalhaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
