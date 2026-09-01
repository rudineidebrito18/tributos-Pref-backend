package br.com.tributos.iptu.domain;

import java.time.LocalDate;
import java.util.UUID;

public record EmitirCertidaoNegativaComando(
    LocalDate validade,
    UUID situacaoCndId,
    String observacao
) {
}
