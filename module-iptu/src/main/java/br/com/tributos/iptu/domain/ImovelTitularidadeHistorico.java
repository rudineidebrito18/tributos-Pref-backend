package br.com.tributos.iptu.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ImovelTitularidadeHistorico(
    UUID id,
    UUID tenantId,
    UUID imovelId,
    UUID contribuinteId,
    TipoRegistroTitularidade tipoRegistro,
    BigDecimal porcentagem,
    Instant dataRegistro
) {
}
