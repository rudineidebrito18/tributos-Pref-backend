package br.com.tributos.iss.adapters.in.web.dto;

import java.util.UUID;

import br.com.tributos.iss.domain.StatusSolicitacao;

public record StatusSolicitacaoResponse(UUID id, String descricao, boolean ativo) {

    public static StatusSolicitacaoResponse de(StatusSolicitacao status) {
        return new StatusSolicitacaoResponse(status.id(), status.descricao(), status.ativo());
    }
}
