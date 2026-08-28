package br.com.tributos.iss.domain;

import java.util.UUID;

public record Tomador(
    UUID id,
    UUID tenantId,
    UUID pessoaId
) {
}
