package br.com.tributos.iss.adapters.in.web.dto;

import java.util.UUID;

import br.com.tributos.iss.domain.Tomador;

public record TomadorResponse(UUID id, UUID pessoaId) {

    public static TomadorResponse de(Tomador tomador) {
        return new TomadorResponse(tomador.id(), tomador.pessoaId());
    }
}
