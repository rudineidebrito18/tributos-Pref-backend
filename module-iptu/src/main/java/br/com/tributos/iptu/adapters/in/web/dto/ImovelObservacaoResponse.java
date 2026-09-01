package br.com.tributos.iptu.adapters.in.web.dto;

import java.time.Instant;
import java.util.UUID;

import br.com.tributos.iptu.application.ImovelObservacaoComUsuario;

public record ImovelObservacaoResponse(
    UUID id,
    String usuario,
    Instant data,
    String observacao
) {

    public static ImovelObservacaoResponse de(ImovelObservacaoComUsuario item) {
        return new ImovelObservacaoResponse(
            item.observacao().id(),
            item.usuario(),
            item.observacao().criadoEm(),
            item.observacao().texto()
        );
    }
}
