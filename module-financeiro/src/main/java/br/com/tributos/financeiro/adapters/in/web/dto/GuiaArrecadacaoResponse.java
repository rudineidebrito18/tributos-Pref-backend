package br.com.tributos.financeiro.adapters.in.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import br.com.tributos.financeiro.domain.GuiaArrecadacao;
import br.com.tributos.financeiro.domain.OrigemGuia;
import br.com.tributos.financeiro.domain.SituacaoGuia;
import br.com.tributos.financeiro.domain.StatusPix;
import br.com.tributos.financeiro.domain.TipoTributo;

public record GuiaArrecadacaoResponse(
    UUID id,
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
    StatusPix statusPix,
    String statusPixDescricao,
    String formaPagamentoCodigo
) {
    public static GuiaArrecadacaoResponse de(GuiaArrecadacao g, String formaPagamentoCodigo) {
        return new GuiaArrecadacaoResponse(
            g.id(),
            g.numero(),
            g.tipoTributo(),
            g.origemTipo(),
            g.origemId(),
            g.contribuinteId(),
            g.competenciaMes(),
            g.competenciaAno(),
            g.dataEmissao(),
            g.dataVencimento(),
            g.valor(),
            g.situacao(),
            g.formaPagamentoId(),
            g.dataEfetivacao(),
            g.valorPago(),
            g.codigoBarras(),
            g.pixTxid(),
            g.descricaoAvulsa(),
            g.statusPix(),
            g.statusPix() != null ? g.statusPix().descricaoLegado() : null,
            formaPagamentoCodigo
        );
    }
}
