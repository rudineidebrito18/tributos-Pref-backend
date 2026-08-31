package br.com.tributos.cadastro.adapters.in.web.dto;

import java.util.UUID;

import br.com.tributos.cadastro.domain.Logradouro;

public record LogradouroResponse(
    UUID id,
    UUID cidadeId,
    UUID bairroId,
    String tipo,
    String nome,
    String cep
) {
    public static LogradouroResponse de(Logradouro logradouro) {
        return new LogradouroResponse(
            logradouro.id(), logradouro.cidadeId(), logradouro.bairroId(),
            logradouro.tipo(), logradouro.nome(), logradouro.cep()
        );
    }
}
