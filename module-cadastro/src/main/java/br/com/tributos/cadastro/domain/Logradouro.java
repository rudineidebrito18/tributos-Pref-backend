package br.com.tributos.cadastro.domain;

import java.time.Instant;
import java.util.UUID;

public record Logradouro(
    UUID id,
    UUID tenantId,
    UUID cidadeId,
    UUID bairroId,
    String tipo,
    String nome,
    String cep,
    Instant criadoEm
) {
}
