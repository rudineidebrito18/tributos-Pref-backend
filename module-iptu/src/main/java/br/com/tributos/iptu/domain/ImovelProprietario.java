package br.com.tributos.iptu.domain;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;

import br.com.tributos.kernel.exception.RegraNegocioException;

public record ImovelProprietario(
    UUID id,
    UUID tenantId,
    UUID imovelId,
    UUID contribuinteId,
    BigDecimal porcentagem,
    boolean proprietarioPrincipal
) {

    private static final BigDecimal CEM = new BigDecimal("100");

    public static void validarComposicao(Collection<ImovelProprietario> proprietarios) {
        if (proprietarios == null || proprietarios.isEmpty()) {
            throw new RegraNegocioException("Informe ao menos um proprietário.");
        }

        long principais = proprietarios.stream().filter(ImovelProprietario::proprietarioPrincipal).count();
        if (principais != 1) {
            throw new RegraNegocioException("Deve haver exatamente um proprietário principal.");
        }

        BigDecimal soma = proprietarios.stream()
            .map(ImovelProprietario::porcentagem)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (soma.compareTo(CEM) != 0) {
            throw new RegraNegocioException("A soma das porcentagens dos proprietários deve ser exatamente 100%.");
        }
    }

    public static void validarComposicaoParcial(Collection<ImovelProprietario> proprietarios) {
        if (proprietarios == null || proprietarios.isEmpty()) {
            return;
        }

        long principais = proprietarios.stream().filter(ImovelProprietario::proprietarioPrincipal).count();
        if (principais > 1) {
            throw new RegraNegocioException("Deve haver no máximo um proprietário principal.");
        }

        BigDecimal soma = proprietarios.stream()
            .map(ImovelProprietario::porcentagem)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (soma.compareTo(CEM) > 0) {
            throw new RegraNegocioException("A soma das porcentagens dos proprietários não pode exceder 100%.");
        }
    }
}
