package br.com.tributos.cadastro.adapters.in.web.dto;

import java.time.Instant;
import java.util.UUID;

import br.com.tributos.cadastro.domain.Documento;

public record DocumentoSistemaResponse(
    UUID id,
    String titulo,
    UUID categoriaId,
    String categoriaNome,
    String nomeArquivo,
    Instant criadoEm
) {

    public static DocumentoSistemaResponse de(Documento documento, String categoriaNome) {
        return new DocumentoSistemaResponse(
            documento.id(),
            documento.titulo(),
            documento.categoriaId(),
            categoriaNome,
            documento.nomeArquivo(),
            documento.criadoEm()
        );
    }
}
