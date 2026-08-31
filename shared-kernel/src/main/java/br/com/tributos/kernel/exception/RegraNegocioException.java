package br.com.tributos.kernel.exception;

/** Violação de regra de negócio antes de chamar integração externa (HTTP 422). */
public class RegraNegocioException extends ValidationException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
