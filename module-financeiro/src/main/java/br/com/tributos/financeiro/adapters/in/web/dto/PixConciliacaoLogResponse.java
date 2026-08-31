package br.com.tributos.financeiro.adapters.in.web.dto;

import java.time.Instant;
import java.util.UUID;

import br.com.tributos.financeiro.domain.OrigemConciliacaoPix;
import br.com.tributos.financeiro.domain.PixConciliacaoLog;

public record PixConciliacaoLogResponse(
    UUID id,
    UUID guiaId,
    String txid,
    String endToEndId,
    String statusAnterior,
    String statusNovo,
    OrigemConciliacaoPix origem,
    Instant criadoEm
) {
    public static PixConciliacaoLogResponse de(PixConciliacaoLog log) {
        return new PixConciliacaoLogResponse(
            log.id(),
            log.guiaId(),
            log.txid(),
            log.endToEndId(),
            log.statusAnterior(),
            log.statusNovo(),
            log.origem(),
            log.criadoEm()
        );
    }
}
