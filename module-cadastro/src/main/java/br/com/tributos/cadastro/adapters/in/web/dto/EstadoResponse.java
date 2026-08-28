package br.com.tributos.cadastro.adapters.in.web.dto;

import java.util.UUID;

import br.com.tributos.cadastro.domain.Cidade;
import br.com.tributos.cadastro.domain.Estado;

public record EstadoResponse(UUID id, String sigla, String nome) {

    public static EstadoResponse de(Estado estado) {
        return new EstadoResponse(estado.id(), estado.sigla(), estado.nome());
    }
}
