package br.com.tributos.iptu.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record HabiteseImovel(
    UUID id,
    UUID tenantId,
    UUID imovelId,
    UUID tipoId,
    long numero,
    LocalDate dataEmissao,
    Instant dataEmissaoTs
) {
}
