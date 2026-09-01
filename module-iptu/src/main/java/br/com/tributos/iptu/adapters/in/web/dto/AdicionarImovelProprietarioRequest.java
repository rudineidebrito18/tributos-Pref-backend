package br.com.tributos.iptu.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.tributos.iptu.domain.ImovelProprietario;

import jakarta.validation.constraints.NotNull;

public record AdicionarImovelProprietarioRequest(
    @NotNull(message = "Informe o contribuinte.")
    UUID contribuinteId,
    @NotNull(message = "Informe a porcentagem.")
    BigDecimal porcentagem,
    boolean proprietarioPrincipal
) {
}
