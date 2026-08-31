package br.com.tributos.iss.adapters.in.web.dto;

import java.util.UUID;

import br.com.tributos.iss.domain.GrupoServico;

public record GrupoServicoResponse(UUID id, String codigo, String descricao) {

    public static GrupoServicoResponse de(GrupoServico grupo) {
        return new GrupoServicoResponse(grupo.id(), grupo.codigo(), grupo.descricao());
    }
}
