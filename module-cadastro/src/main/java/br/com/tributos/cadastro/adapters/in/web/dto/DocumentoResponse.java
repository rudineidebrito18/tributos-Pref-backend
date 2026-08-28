package br.com.tributos.cadastro.adapters.in.web.dto;

import java.time.Instant;
import java.util.UUID;

import br.com.tributos.cadastro.domain.Documento;

public record DocumentoResponse(
    UUID id,
    String tipo,
    String nomeArquivo,
    String conteudoTipo,
    long tamanhoBytes,
    Instant criadoEm
) {

    public static DocumentoResponse de(Documento documento) {
        return new DocumentoResponse(
            documento.id(),
            documento.tipo(),
            documento.nomeArquivo(),
            documento.conteudoTipo(),
            documento.tamanhoBytes(),
            documento.criadoEm()
        );
    }
}
