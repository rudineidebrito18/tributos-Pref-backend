package br.com.tributos.iptu.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ImovelDestinacao(
    UUID id,
    UUID tenantId,
    String nome,
    boolean ativo,
    UUID tipoImovelId,
    BigDecimal aliquotaIptu,
    Instant criadoEm
) {
}
