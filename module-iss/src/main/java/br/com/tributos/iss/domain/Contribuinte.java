package br.com.tributos.iss.domain;

import java.util.UUID;

public record Contribuinte(
    UUID id,
    UUID tenantId,
    UUID pessoaId,
    String inscricaoMunicipal,
    UUID tipoContribuinteId,
    UUID situacaoCadastralId,
    UUID statusCredenciamentoId,
    UUID regimeTributarioId,
    String nomeContador,
    String emailContador
) {
}
