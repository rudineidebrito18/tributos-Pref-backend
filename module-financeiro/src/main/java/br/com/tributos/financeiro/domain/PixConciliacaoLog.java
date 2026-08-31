package br.com.tributos.financeiro.domain;

import java.time.Instant;
import java.util.UUID;

public record PixConciliacaoLog(
    UUID id,
    UUID tenantId,
    UUID guiaId,
    String txid,
    String endToEndId,
    String statusAnterior,
    String statusNovo,
    OrigemConciliacaoPix origem,
    String payloadBruto,
    Instant criadoEm
) {
}
