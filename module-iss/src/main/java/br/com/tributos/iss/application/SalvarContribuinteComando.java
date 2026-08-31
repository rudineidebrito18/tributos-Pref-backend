package br.com.tributos.iss.application;

import java.util.UUID;

public record SalvarContribuinteComando(
    UUID pessoaId,
    String inscricaoMunicipal,
    UUID tipoContribuinteId,
    UUID situacaoCadastralId,
    UUID regimeTributarioId,
    String nomeFantasia,
    String inscricaoEstadual,
    String contato,
    String telefone2,
    String emailNota,
    UUID usuarioId,
    String nomeContador,
    String emailContador
) {
}
