package br.com.tributos.iss.domain;

import java.time.LocalDate;

import br.com.tributos.kernel.exception.ValidationException;

public final class ValidadorVigenciaDocumento {

    private ValidadorVigenciaDocumento() {
    }

    public static boolean estaVigente(LocalDate validade, LocalDate referencia) {
        return !validade.isBefore(referencia);
    }

    public static void validarPeriodoAlvara(LocalDate dataExpedicao, LocalDate validade) {
        if (dataExpedicao == null || validade == null) {
            throw new ValidationException("Informe a data de expedição e a validade do alvará.");
        }
        if (validade.isBefore(dataExpedicao)) {
            throw new ValidationException("A validade do alvará não pode ser anterior à data de expedição.");
        }
    }
}
