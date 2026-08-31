package br.com.tributos.cadastro.domain;

import java.time.Instant;
import java.util.UUID;

public record DocumentoCompartilhamento(
    UUID id,
    UUID tenantId,
    UUID documentoId,
    UUID usuarioId,
    Instant criadoEm
) {
}
