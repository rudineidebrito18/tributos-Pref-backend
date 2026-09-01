package br.com.tributos.itbi.domain;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;

import br.com.tributos.kernel.exception.RegraNegocioException;

public record ParteTransmissao(
    UUID id,
    UUID tenantId,
    UUID guiaId,
    UUID contribuinteId,
    PapelParteTransmissao papel,
    BigDecimal porcentagem,
    boolean principal
) {

    private static final BigDecimal CEM = new BigDecimal("100");

    public static void validarComposicao(Collection<ParteTransmissao> partes) {
        if (partes == null || partes.isEmpty()) {
            throw new RegraNegocioException("Informe ao menos uma parte da transmissão.");
        }

        long principais = partes.stream().filter(ParteTransmissao::principal).count();
        if (principais != 1) {
            throw new RegraNegocioException("Deve haver exatamente um principal.");
        }

        BigDecimal soma = partes.stream()
            .map(ParteTransmissao::porcentagem)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (soma.compareTo(CEM) != 0) {
            throw new RegraNegocioException("A soma das porcentagens deve ser exatamente 100%.");
        }
    }

    public static void validarComposicaoParcial(Collection<ParteTransmissao> partes) {
        if (partes == null || partes.isEmpty()) {
            return;
        }

        long principais = partes.stream().filter(ParteTransmissao::principal).count();
        if (principais > 1) {
            throw new RegraNegocioException("Deve haver no máximo um principal.");
        }

        BigDecimal soma = partes.stream()
            .map(ParteTransmissao::porcentagem)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (soma.compareTo(CEM) > 0) {
            throw new RegraNegocioException("A soma das porcentagens não pode exceder 100%.");
        }
    }

    public static void validarGuiaCompleta(
        Collection<ParteTransmissao> transmitentes,
        Collection<ParteTransmissao> adquirentes
    ) {
        if (transmitentes == null || transmitentes.isEmpty()) {
            throw new RegraNegocioException("Informe ao menos um transmitente.");
        }
        if (adquirentes == null || adquirentes.isEmpty()) {
            throw new RegraNegocioException("Informe ao menos um adquirente.");
        }
        validarComposicao(transmitentes);
        validarComposicao(adquirentes);
    }
}
