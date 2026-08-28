package br.com.tributos.iptu.adapters.in.web.dto;

import java.time.LocalDate;

public record EmitirCertidaoNegativaRequest(
    LocalDate validade
) {
}
