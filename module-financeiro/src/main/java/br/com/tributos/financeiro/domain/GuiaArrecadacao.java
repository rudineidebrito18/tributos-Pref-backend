package br.com.tributos.financeiro.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record GuiaArrecadacao(
    UUID id,
    UUID tenantId,
    long numero,
    TipoTributo tipoTributo,
    OrigemGuia origemTipo,
    UUID origemId,
    UUID contribuinteId,
    Integer competenciaMes,
    Integer competenciaAno,
    Instant dataEmissao,
    LocalDate dataVencimento,
    BigDecimal valor,
    SituacaoGuia situacao,
    UUID formaPagamentoId,
    Instant dataEfetivacao,
    BigDecimal valorPago,
    String codigoBarras,
    String pixTxid,
    String descricaoAvulsa,
    String codigoVerificacao,
    StatusPix statusPix,
    String pixQrcodePayload,
    String pixLink,
    String pixEndToEndId,
    Instant pixSolicitadoEm
) {
}
