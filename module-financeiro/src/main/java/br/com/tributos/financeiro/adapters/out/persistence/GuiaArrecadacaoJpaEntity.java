package br.com.tributos.financeiro.adapters.out.persistence;

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

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import br.com.tributos.financeiro.domain.OrigemGuia;
import br.com.tributos.financeiro.domain.SituacaoGuia;
import br.com.tributos.financeiro.domain.StatusPix;
import br.com.tributos.financeiro.domain.TipoTributo;

@Entity
@Table(name = "guia_arrecadacao")
public class GuiaArrecadacaoJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private long numero;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_tributo", nullable = false, length = 20)
    private TipoTributo tipoTributo;

    @Enumerated(EnumType.STRING)
    @Column(name = "origem_tipo", length = 40)
    private OrigemGuia origemTipo;

    @Column(name = "origem_id")
    private UUID origemId;

    @Column(name = "contribuinte_id", nullable = false)
    private UUID contribuinteId;

    @JdbcTypeCode(SqlTypes.SMALLINT)
    @Column(name = "competencia_mes")
    private Integer competenciaMes;

    @JdbcTypeCode(SqlTypes.SMALLINT)
    @Column(name = "competencia_ano")
    private Integer competenciaAno;

    @Column(name = "data_emissao", nullable = false)
    private Instant dataEmissao;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SituacaoGuia situacao;

    @Column(name = "forma_pagamento_id")
    private UUID formaPagamentoId;

    @Column(name = "data_efetivacao")
    private Instant dataEfetivacao;

    @Column(name = "valor_pago", precision = 14, scale = 2)
    private BigDecimal valorPago;

    @Column(name = "codigo_barras", length = 100)
    private String codigoBarras;

    @Column(name = "pix_txid", length = 100)
    private String pixTxid;

    @Column(name = "descricao_avulsa")
    private String descricaoAvulsa;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pix", length = 40)
    private StatusPix statusPix;

    @Column(name = "pix_qrcode_payload")
    private String pixQrcodePayload;

    @Column(name = "pix_link")
    private String pixLink;

    @Column(name = "pix_end_to_end_id", length = 40)
    private String pixEndToEndId;

    @Column(name = "pix_solicitado_em")
    private Instant pixSolicitadoEm;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public long getNumero() {
        return numero;
    }

    public void setNumero(long numero) {
        this.numero = numero;
    }

    public TipoTributo getTipoTributo() {
        return tipoTributo;
    }

    public void setTipoTributo(TipoTributo tipoTributo) {
        this.tipoTributo = tipoTributo;
    }

    public OrigemGuia getOrigemTipo() {
        return origemTipo;
    }

    public void setOrigemTipo(OrigemGuia origemTipo) {
        this.origemTipo = origemTipo;
    }

    public UUID getOrigemId() {
        return origemId;
    }

    public void setOrigemId(UUID origemId) {
        this.origemId = origemId;
    }

    public UUID getContribuinteId() {
        return contribuinteId;
    }

    public void setContribuinteId(UUID contribuinteId) {
        this.contribuinteId = contribuinteId;
    }

    public Integer getCompetenciaMes() {
        return competenciaMes;
    }

    public void setCompetenciaMes(Integer competenciaMes) {
        this.competenciaMes = competenciaMes;
    }

    public Integer getCompetenciaAno() {
        return competenciaAno;
    }

    public void setCompetenciaAno(Integer competenciaAno) {
        this.competenciaAno = competenciaAno;
    }

    public Instant getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Instant dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public SituacaoGuia getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoGuia situacao) {
        this.situacao = situacao;
    }

    public UUID getFormaPagamentoId() {
        return formaPagamentoId;
    }

    public void setFormaPagamentoId(UUID formaPagamentoId) {
        this.formaPagamentoId = formaPagamentoId;
    }

    public Instant getDataEfetivacao() {
        return dataEfetivacao;
    }

    public void setDataEfetivacao(Instant dataEfetivacao) {
        this.dataEfetivacao = dataEfetivacao;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getPixTxid() {
        return pixTxid;
    }

    public void setPixTxid(String pixTxid) {
        this.pixTxid = pixTxid;
    }

    public String getDescricaoAvulsa() {
        return descricaoAvulsa;
    }

    public void setDescricaoAvulsa(String descricaoAvulsa) {
        this.descricaoAvulsa = descricaoAvulsa;
    }

    public StatusPix getStatusPix() {
        return statusPix;
    }

    public void setStatusPix(StatusPix statusPix) {
        this.statusPix = statusPix;
    }

    public String getPixQrcodePayload() {
        return pixQrcodePayload;
    }

    public void setPixQrcodePayload(String pixQrcodePayload) {
        this.pixQrcodePayload = pixQrcodePayload;
    }

    public String getPixLink() {
        return pixLink;
    }

    public void setPixLink(String pixLink) {
        this.pixLink = pixLink;
    }

    public String getPixEndToEndId() {
        return pixEndToEndId;
    }

    public void setPixEndToEndId(String pixEndToEndId) {
        this.pixEndToEndId = pixEndToEndId;
    }

    public Instant getPixSolicitadoEm() {
        return pixSolicitadoEm;
    }

    public void setPixSolicitadoEm(Instant pixSolicitadoEm) {
        this.pixSolicitadoEm = pixSolicitadoEm;
    }
}
