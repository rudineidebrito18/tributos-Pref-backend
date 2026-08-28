package br.com.tributos.iss.adapters.in.web.dto;

import java.util.UUID;

import br.com.tributos.iss.domain.CatalogoIss;

public record CatalogoIssResponse(UUID id, String nome, boolean ativo) {

    public static CatalogoIssResponse de(CatalogoIss item) {
        return new CatalogoIssResponse(item.id(), item.nome(), item.ativo());
    }
}
