package br.com.tributos.iptu.adapters.in.web.dto;

import java.util.UUID;

import br.com.tributos.iptu.domain.CatalogoIptu;

public record CatalogoIptuResponse(UUID id, String nome, boolean ativo) {

    public static CatalogoIptuResponse de(CatalogoIptu item) {
        return new CatalogoIptuResponse(item.id(), item.nome(), item.ativo());
    }
}
