package br.com.tributos.iss.adapters.in.web.dto;

import java.util.UUID;

import br.com.tributos.iss.domain.TipoSolicitacao;

public record TipoSolicitacaoResponse(UUID id, String descricao, UUID usuarioNotificarId, boolean ativo) {

    public static TipoSolicitacaoResponse de(TipoSolicitacao tipo) {
        return new TipoSolicitacaoResponse(
            tipo.id(), tipo.descricao(), tipo.usuarioNotificarId(), tipo.ativo()
        );
    }
}
