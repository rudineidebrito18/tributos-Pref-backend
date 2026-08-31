package br.com.tributos.cadastro.domain;

import java.time.Instant;
import java.util.UUID;

public record Documento(
    UUID id,
    UUID tenantId,
    UUID pessoaId,
    String tipo,
    String titulo,
    UUID categoriaId,
    String nomeArquivo,
    String conteudoTipo,
    long tamanhoBytes,
    String storageChave,
    boolean compartilhado,
    Instant criadoEm
) {
}
