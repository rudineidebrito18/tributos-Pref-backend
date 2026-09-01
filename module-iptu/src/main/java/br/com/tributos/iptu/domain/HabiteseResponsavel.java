package br.com.tributos.iptu.domain;

import java.util.UUID;

public record HabiteseResponsavel(
    UUID id,
    short ordem,
    String nome,
    String profissao,
    String documento
) {
}
