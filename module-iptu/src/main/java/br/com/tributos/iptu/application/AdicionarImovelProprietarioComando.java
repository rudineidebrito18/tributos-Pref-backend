package br.com.tributos.iptu.application;

import java.math.BigDecimal;
import java.util.UUID;

public record AdicionarImovelProprietarioComando(
    UUID contribuinteId,
    BigDecimal porcentagem,
    boolean proprietarioPrincipal
) {
}
