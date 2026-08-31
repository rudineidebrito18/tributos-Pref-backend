package br.com.tributos.iss.application;

import java.util.UUID;

public record ContribuinteListagemItem(
    UUID id,
    String cpfCnpj,
    String nome,
    String email,
    String status,
    String situacaoCadastral
) {
}
