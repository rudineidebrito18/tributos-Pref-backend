package br.com.tributos.iptu.domain;

import java.time.Instant;
import java.util.UUID;

public record ImovelObservacao(
    UUID id,
    UUID tenantId,
    UUID imovelId,
    UUID usuarioId,
    String texto,
    Instant criadoEm
) {
}
