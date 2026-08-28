package br.com.tributos.iptu.domain;

import java.time.LocalDate;

import br.com.tributos.kernel.exception.ValidationException;

public final class ValidadorVigenciaDocumento {

    private ValidadorVigenciaDocumento() {
    }

    public static boolean estaVigente(LocalDate validade, LocalDate referencia) {
        return !validade.isBefore(referencia);
    }

    public static void validarPeriodoCertidao(LocalDate dataEmissao, LocalDate validade) {
        if (dataEmissao == null || validade == null) {
            throw new ValidationException("Informe a data de emissão e a validade da certidão.");
        }
        if (validade.isBefore(dataEmissao)) {
            throw new ValidationException("A validade da certidão não pode ser anterior à data de emissão.");
        }
    }
}
