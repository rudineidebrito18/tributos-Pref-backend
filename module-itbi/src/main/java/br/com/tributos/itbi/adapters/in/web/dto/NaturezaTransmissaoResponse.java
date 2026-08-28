package br.com.tributos.itbi.adapters.in.web.dto;

import java.util.UUID;

import br.com.tributos.itbi.domain.NaturezaTransmissao;

public record NaturezaTransmissaoResponse(UUID id, String nome) {
    public static NaturezaTransmissaoResponse de(NaturezaTransmissao n) {
        return new NaturezaTransmissaoResponse(n.id(), n.nome());
    }
}
