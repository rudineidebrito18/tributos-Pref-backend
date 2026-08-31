package br.com.tributos.cadastro.adapters.in.web.dto;

import java.time.Instant;
import java.util.UUID;

import br.com.tributos.cadastro.domain.DocumentoCategoria;

public record DocumentoCategoriaResponse(
    UUID id,
    String nome,
    Instant criadoEm
) {

    public static DocumentoCategoriaResponse de(DocumentoCategoria categoria) {
        return new DocumentoCategoriaResponse(categoria.id(), categoria.nome(), categoria.criadoEm());
    }
}
