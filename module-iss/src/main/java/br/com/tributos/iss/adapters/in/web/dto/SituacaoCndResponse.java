package br.com.tributos.iss.adapters.in.web.dto;

import java.util.UUID;

import br.com.tributos.iss.domain.SituacaoCnd;

public record SituacaoCndResponse(UUID id, String descricao, String titulo, boolean ativo) {

    public static SituacaoCndResponse de(SituacaoCnd situacao) {
        return new SituacaoCndResponse(
            situacao.id(), situacao.descricao(), situacao.titulo(), situacao.ativo()
        );
    }
}
