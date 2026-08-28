package br.com.tributos.iss.adapters.in.web.dto;

import java.time.Instant;
import java.util.UUID;

import br.com.tributos.iss.domain.SolicitacaoCredenciamento;

public record SolicitacaoCredenciamentoResponse(
    UUID id,
    UUID contribuinteId,
    UUID statusId,
    String observacao,
    UUID analisadoPor,
    Instant analisadoEm,
    Instant criadoEm
) {

    public static SolicitacaoCredenciamentoResponse de(SolicitacaoCredenciamento solicitacao) {
        return new SolicitacaoCredenciamentoResponse(
            solicitacao.id(),
            solicitacao.contribuinteId(),
            solicitacao.statusId(),
            solicitacao.observacao(),
            solicitacao.analisadoPor(),
            solicitacao.analisadoEm(),
            solicitacao.criadoEm()
        );
    }
}
