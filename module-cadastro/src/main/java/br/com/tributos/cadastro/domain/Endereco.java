package br.com.tributos.cadastro.domain;

import java.util.UUID;

public record Endereco(
    UUID id,
    UUID tenantId,
    UUID pessoaId,
    String logradouroTexto,
    String numero,
    String complemento,
    String bairroTexto,
    UUID cidadeId,
    String cep,
    boolean principal
) {
}
