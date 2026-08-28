package br.com.tributos.iss.adapters.in.web.dto;

import java.util.UUID;

import br.com.tributos.iss.domain.Atividade;

public record AtividadeResponse(UUID id, String codigo, String descricao, boolean ativo) {

    public static AtividadeResponse de(Atividade atividade) {
        return new AtividadeResponse(atividade.id(), atividade.codigo(), atividade.descricao(), atividade.ativo());
    }
}
