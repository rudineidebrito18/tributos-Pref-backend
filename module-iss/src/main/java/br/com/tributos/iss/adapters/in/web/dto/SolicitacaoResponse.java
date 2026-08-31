package br.com.tributos.iss.adapters.in.web.dto;

import java.time.Instant;
import java.util.UUID;

import br.com.tributos.iss.domain.Solicitacao;

public record SolicitacaoResponse(
    UUID id,
    UUID usuarioId,
    UUID tipoSolicitacaoId,
    UUID statusSolicitacaoId,
    String descricao,
    Instant dataHora
) {

    public static SolicitacaoResponse de(Solicitacao solicitacao) {
        return new SolicitacaoResponse(
            solicitacao.id(),
            solicitacao.usuarioId(),
            solicitacao.tipoSolicitacaoId(),
            solicitacao.statusSolicitacaoId(),
            solicitacao.descricao(),
            solicitacao.dataHora()
        );
    }
}
