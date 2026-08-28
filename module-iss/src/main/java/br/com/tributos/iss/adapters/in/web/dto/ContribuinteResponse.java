package br.com.tributos.iss.adapters.in.web.dto;

import java.util.UUID;

import br.com.tributos.iss.domain.Contribuinte;

public record ContribuinteResponse(
    UUID id,
    UUID pessoaId,
    String inscricaoMunicipal,
    UUID tipoContribuinteId,
    UUID situacaoCadastralId,
    UUID statusCredenciamentoId,
    UUID regimeTributarioId,
    String nomeContador,
    String emailContador
) {

    public static ContribuinteResponse de(Contribuinte contribuinte) {
        return new ContribuinteResponse(
            contribuinte.id(),
            contribuinte.pessoaId(),
            contribuinte.inscricaoMunicipal(),
            contribuinte.tipoContribuinteId(),
            contribuinte.situacaoCadastralId(),
            contribuinte.statusCredenciamentoId(),
            contribuinte.regimeTributarioId(),
            contribuinte.nomeContador(),
            contribuinte.emailContador()
        );
    }
}
