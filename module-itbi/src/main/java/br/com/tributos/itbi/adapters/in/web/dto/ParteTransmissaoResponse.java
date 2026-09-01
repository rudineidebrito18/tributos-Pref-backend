package br.com.tributos.itbi.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.tributos.itbi.domain.PapelParteTransmissao;
import br.com.tributos.itbi.domain.ParteTransmissao;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ParteTransmissaoResponse(
    UUID id,
    UUID guiaId,
    UUID contribuinteId,
    PapelParteTransmissao papel,
    BigDecimal porcentagem,
    boolean principal
) {
    public static ParteTransmissaoResponse de(ParteTransmissao parte) {
        return new ParteTransmissaoResponse(
            parte.id(),
            parte.guiaId(),
            parte.contribuinteId(),
            parte.papel(),
            parte.porcentagem(),
            parte.principal()
        );
    }
}
