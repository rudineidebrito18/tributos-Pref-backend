package br.com.tributos.itbi.adapters.out.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import br.com.tributos.itbi.domain.SituacaoGuiaItbi;
import br.com.tributos.itbi.domain.TipoTributacaoItbi;

@Entity
@Table(name = "itbi_guia")
public class GuiaItbiJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private long numero;

    @Column(name = "imovel_id", nullable = false)
    private UUID imovelId;

    @Column(name = "tipo_guia_id", nullable = false)
    private UUID tipoGuiaId;

    @Column(name = "natureza_transmissao_id", nullable = false)
    private UUID naturezaTransmissaoId;

    @Column(name = "data_solicitacao", nullable = false)
    private Instant dataSolicitacao;

    @Column(name = "valor_transacao", nullable = false, precision = 14, scale = 2)
    private BigDecimal valorTransacao;

    @Column(name = "valor_venal_referencia", nullable = false, precision = 14, scale = 2)
    private BigDecimal valorVenalReferencia;

    @Column(name = "base_calculo", nullable = false, precision = 14, scale = 2)
    private BigDecimal baseCalculo;

    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal aliquota;

    @Column(name = "valor_itbi", nullable = false, precision = 14, scale = 2)
    private BigDecimal valorItbi;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SituacaoGuiaItbi situacao;

    @Column(name = "transferencia_titularidade_realizada", nullable = false)
    private boolean transferenciaTitularidadeRealizada;

    @Column(name = "data_transacao")
    private LocalDate dataTransacao;

    @Column(name = "percentual_transmitido", nullable = false, precision = 6, scale = 2)
    private BigDecimal percentualTransmitido;

    @Column(name = "valor_nao_financiado", nullable = false, precision = 14, scale = 2)
    private BigDecimal valorNaoFinanciado;

    @Column(name = "valor_financiado", nullable = false, precision = 14, scale = 2)
    private BigDecimal valorFinanciado;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal desconto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_tributacao", length = 20)
    private TipoTributacaoItbi tipoTributacao;

    private String observacao;

    @Column(name = "motivo_cancelamento")
    private String motivoCancelamento;

    @Column(name = "codigo_verificacao", length = 32)
    private String codigoVerificacao;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
    public long getNumero() { return numero; }
    public void setNumero(long numero) { this.numero = numero; }
    public UUID getImovelId() { return imovelId; }
    public void setImovelId(UUID imovelId) { this.imovelId = imovelId; }
    public UUID getTipoGuiaId() { return tipoGuiaId; }
    public void setTipoGuiaId(UUID tipoGuiaId) { this.tipoGuiaId = tipoGuiaId; }
    public UUID getNaturezaTransmissaoId() { return naturezaTransmissaoId; }
    public void setNaturezaTransmissaoId(UUID naturezaTransmissaoId) { this.naturezaTransmissaoId = naturezaTransmissaoId; }
    public Instant getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(Instant dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }
    public BigDecimal getValorTransacao() { return valorTransacao; }
    public void setValorTransacao(BigDecimal valorTransacao) { this.valorTransacao = valorTransacao; }
    public BigDecimal getValorVenalReferencia() { return valorVenalReferencia; }
    public void setValorVenalReferencia(BigDecimal valorVenalReferencia) { this.valorVenalReferencia = valorVenalReferencia; }
    public BigDecimal getBaseCalculo() { return baseCalculo; }
    public void setBaseCalculo(BigDecimal baseCalculo) { this.baseCalculo = baseCalculo; }
    public BigDecimal getAliquota() { return aliquota; }
    public void setAliquota(BigDecimal aliquota) { this.aliquota = aliquota; }
    public BigDecimal getValorItbi() { return valorItbi; }
    public void setValorItbi(BigDecimal valorItbi) { this.valorItbi = valorItbi; }
    public SituacaoGuiaItbi getSituacao() { return situacao; }
    public void setSituacao(SituacaoGuiaItbi situacao) { this.situacao = situacao; }
    public boolean isTransferenciaTitularidadeRealizada() { return transferenciaTitularidadeRealizada; }
    public void setTransferenciaTitularidadeRealizada(boolean transferenciaTitularidadeRealizada) {
        this.transferenciaTitularidadeRealizada = transferenciaTitularidadeRealizada;
    }
    public LocalDate getDataTransacao() { return dataTransacao; }
    public void setDataTransacao(LocalDate dataTransacao) { this.dataTransacao = dataTransacao; }
    public BigDecimal getPercentualTransmitido() { return percentualTransmitido; }
    public void setPercentualTransmitido(BigDecimal percentualTransmitido) { this.percentualTransmitido = percentualTransmitido; }
    public BigDecimal getValorNaoFinanciado() { return valorNaoFinanciado; }
    public void setValorNaoFinanciado(BigDecimal valorNaoFinanciado) { this.valorNaoFinanciado = valorNaoFinanciado; }
    public BigDecimal getValorFinanciado() { return valorFinanciado; }
    public void setValorFinanciado(BigDecimal valorFinanciado) { this.valorFinanciado = valorFinanciado; }
    public BigDecimal getDesconto() { return desconto; }
    public void setDesconto(BigDecimal desconto) { this.desconto = desconto; }
    public TipoTributacaoItbi getTipoTributacao() { return tipoTributacao; }
    public void setTipoTributacao(TipoTributacaoItbi tipoTributacao) { this.tipoTributacao = tipoTributacao; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public String getMotivoCancelamento() { return motivoCancelamento; }
    public void setMotivoCancelamento(String motivoCancelamento) { this.motivoCancelamento = motivoCancelamento; }
    public String getCodigoVerificacao() { return codigoVerificacao; }
    public void setCodigoVerificacao(String codigoVerificacao) { this.codigoVerificacao = codigoVerificacao; }
}
