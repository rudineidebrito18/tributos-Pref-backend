package br.com.tributos.cadastro.adapters.in.web.dto;

import java.util.UUID;

import br.com.tributos.cadastro.domain.Cidade;

public record CidadeResponse(UUID id, String nome, String uf, String codigoIbge) {

    public static CidadeResponse de(Cidade cidade) {
        return new CidadeResponse(cidade.id(), cidade.nome(), cidade.uf(), cidade.codigoIbge());
    }
}
