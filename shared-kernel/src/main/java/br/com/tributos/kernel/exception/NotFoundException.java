package br.com.tributos.kernel.exception;

/** Recurso de domínio não encontrado (mapeada para HTTP 404 pelo handler global). */
public class NotFoundException extends DomainException {

    public NotFoundException(String mensagem) {
        super(mensagem);
    }

    public static NotFoundException de(String entidade, Object identificador) {
        return new NotFoundException(entidade + " não encontrado(a): " + identificador);
    }
}
