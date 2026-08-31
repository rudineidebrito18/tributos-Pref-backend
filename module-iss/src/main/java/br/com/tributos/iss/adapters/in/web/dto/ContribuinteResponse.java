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
    String nomeFantasia,
    String inscricaoEstadual,
    String contato,
    String telefone2,
    String emailNota,
    UUID usuarioId,
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
            contribuinte.nomeFantasia(),
            contribuinte.inscricaoEstadual(),
            contribuinte.contato(),
            contribuinte.telefone2(),
            contribuinte.emailNota(),
            contribuinte.usuarioId(),
            contribuinte.nomeContador(),
            contribuinte.emailContador()
        );
    }
}
