package br.com.tributos.iss.application;

import java.util.UUID;

public record SalvarContribuinteComando(
    UUID pessoaId,
    String inscricaoMunicipal,
    UUID tipoContribuinteId,
    UUID situacaoCadastralId,
    UUID regimeTributarioId,
    String nomeContador,
    String emailContador
) {
}
