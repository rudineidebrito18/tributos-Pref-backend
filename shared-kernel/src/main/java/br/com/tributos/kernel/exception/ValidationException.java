package br.com.tributos.kernel.exception;

/**
 * Violação de regra de negócio (não confundir com Bean Validation de DTO, que é tratado
 * na borda web). Ex.: "não é possível gerar lançamento de IPTU sem alíquota parametrizada
 * para o exercício" — mapeada para HTTP 422 pelo handler global.
 */
public class ValidationException extends DomainException {

    public ValidationException(String mensagem) {
        super(mensagem);
    }
}
