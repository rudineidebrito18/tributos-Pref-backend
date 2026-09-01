package br.com.tributos.financeiro.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import br.com.tributos.financeiro.domain.SituacaoGuia;
import br.com.tributos.financeiro.domain.StatusPix;
import br.com.tributos.financeiro.domain.TipoTributo;

/**
 * De-para com {@code form_IssFaturaList} em forms.json L1681-2573:
 * <ul>
 *   <li>{@code pago} → {@link #pago}</li>
 *   <li>{@code cpfcnpj} → {@link #contribuinteId}</li>
 *   <li>{@code pagador} → {@link #pagadorNome}</li>
 *   <li>{@code cpfcnpjpagador} → {@link #pagadorCpfCnpj}</li>
 *   <li>{@code mes} → {@link #competenciaMes}</li>
 *   <li>{@code ano} → {@link #competenciaAno}</li>
 *   <li>{@code status_pix_id} → {@link #statusPix}</li>
 *   <li>{@code valor} → {@link #valor}</li>
 *   <li>{@code data_emisao_inicio} → {@link #dataEmissaoInicio}</li>
 *   <li>{@code data_emissao_fim} → {@link #dataEmissaoFim}</li>
 *   <li>{@code data_pag_inicio} → {@link #dataPagamentoInicio}</li>
 *   <li>{@code data_pag_fim} → {@link #dataPagamentoFim}</li>
 *   <li>{@code codigoconciliacaosolicitante} → {@link #codigoConciliacaoSolicitante}</li>
 *   <li>{@code iss_tipotributo_id} → {@link #tipoTributo}</li>
 *   <li>{@code iss_formapagamento_id} → {@link #formaPagamentoId}</li>
 * </ul>
 */
public record FiltroFaturamento(
    Boolean pago,
    UUID contribuinteId,
    String pagadorNome,
    String pagadorCpfCnpj,
    Integer competenciaMes,
    Integer competenciaAno,
    StatusPix statusPix,
    BigDecimal valor,
    LocalDate dataEmissaoInicio,
    LocalDate dataEmissaoFim,
    LocalDate dataPagamentoInicio,
    LocalDate dataPagamentoFim,
    String codigoConciliacaoSolicitante,
    TipoTributo tipoTributo,
    UUID formaPagamentoId
) {
}
