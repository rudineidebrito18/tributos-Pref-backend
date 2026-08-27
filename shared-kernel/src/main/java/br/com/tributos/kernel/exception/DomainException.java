package br.com.tributos.kernel.exception;

/**
 * Raiz das exceções de regra de negócio lançadas pelo domínio (não por infraestrutura).
 * O {@code @ExceptionHandler} global em app-bootstrap mapeia subtipos desta classe para
 * respostas HTTP apropriadas (ver PLANEJAMENTO_PROJETO.md §7.2), sem que o domínio precise
 * conhecer HTTP.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String mensagem) {
        super(mensagem);
    }

    protected DomainException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
