package br.com.tributos.iptu.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.tributos.iptu.domain.ImovelProprietario;

public record ImovelProprietarioResponse(
    UUID id,
    UUID contribuinteId,
    BigDecimal porcentagem,
    boolean proprietarioPrincipal
) {

    public static ImovelProprietarioResponse de(ImovelProprietario proprietario) {
        return new ImovelProprietarioResponse(
            proprietario.id(),
            proprietario.contribuinteId(),
            proprietario.porcentagem(),
            proprietario.proprietarioPrincipal()
        );
    }
}
