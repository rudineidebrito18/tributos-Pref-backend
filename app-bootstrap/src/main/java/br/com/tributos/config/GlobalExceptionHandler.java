package br.com.tributos.config;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.tributos.kernel.exception.AutenticacaoException;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.shared.exportacao.ExportacaoLimiteExcedidoException;

/**
 * Traduz exceções de domínio (shared-kernel) para HTTP — o único lugar do backend que
 * conhece os dois vocabulários. Nenhum módulo de domínio precisa saber que existe HTTP.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErroResponse> tratarNaoEncontrado(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErroResponse.de(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(ValidationException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(ErroResponse.de(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage()));
    }

    @ExceptionHandler(AutenticacaoException.class)
    public ResponseEntity<ErroResponse> tratarAutenticacao(AutenticacaoException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErroResponse.de(HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }

    @ExceptionHandler(ExportacaoLimiteExcedidoException.class)
    public ResponseEntity<ErroResponse> tratarLimiteExportacao(ExportacaoLimiteExcedidoException ex) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
            .body(ErroResponse.de(HttpStatus.PAYLOAD_TOO_LARGE, ex.getMessage()));
    }

    public record ErroResponse(Instant timestamp, int status, String erro, String mensagem) {
        static ErroResponse de(HttpStatus status, String mensagem) {
            return new ErroResponse(Instant.now(), status.value(), status.getReasonPhrase(), mensagem);
        }
    }
}
