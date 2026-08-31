package br.com.tributos.cadastro.domain;

import java.time.Instant;
import java.util.UUID;

public record DocumentoCategoria(
    UUID id,
    UUID tenantId,
    String nome,
    Instant criadoEm
) {
}
