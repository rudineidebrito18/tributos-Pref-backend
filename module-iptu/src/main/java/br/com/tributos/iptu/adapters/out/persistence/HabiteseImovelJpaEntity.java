package br.com.tributos.iptu.adapters.out.persistence;

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

import br.com.tributos.iptu.domain.SituacaoFiscalHabitese;

@Entity
@Table(name = "imovel_habitese")
public class HabiteseImovelJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "imovel_id", nullable = false)
    private UUID imovelId;

    @Column(name = "tipo_id", nullable = false)
    private UUID tipoId;

    @Column(nullable = false)
    private long numero;

    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @Column(name = "data_emissao_ts", nullable = false, insertable = false, updatable = false)
    private Instant dataEmissaoTs;

    private Short ano;

    private LocalDate validade;

    @Column(name = "contribuinte_id")
    private UUID contribuinteId;

    @Column(name = "area_imovel", precision = 14, scale = 4)
    private BigDecimal areaImovel;

    @Column(name = "data_conclusao")
    private LocalDate dataConclusao;

    @Column(name = "numero_alvara", length = 50)
    private String numeroAlvara;

    @Column(name = "data_alvara")
    private LocalDate dataAlvara;

    @Column(name = "validade_alvara")
    private LocalDate validadeAlvara;

    @Column(name = "valor_base_calculo", precision = 14, scale = 2)
    private BigDecimal valorBaseCalculo;

    @Column(name = "base_calculo", precision = 14, scale = 2)
    private BigDecimal baseCalculo;

    @Column(precision = 14, scale = 2, nullable = false)
    private BigDecimal desconto = BigDecimal.ZERO;

    @Column(precision = 14, scale = 2)
    private BigDecimal valor;

    @Column(precision = 10, scale = 2)
    private BigDecimal frente;

    @Column(precision = 10, scale = 2)
    private BigDecimal fundos;

    @Column(name = "lado_esquerdo", precision = 10, scale = 2)
    private BigDecimal ladoEsquerdo;

    @Column(name = "lado_direito", precision = 10, scale = 2)
    private BigDecimal ladoDireito;

    private String observacao;

    @Column(name = "codigo_verificacao", length = 32)
    private String codigoVerificacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao_fiscal", length = 20)
    private SituacaoFiscalHabitese situacaoFiscal;

    protected HabiteseImovelJpaEntity() {
    }

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

    public UUID getImovelId() {
        return imovelId;
    }

    public void setImovelId(UUID imovelId) {
        this.imovelId = imovelId;
    }

    public UUID getTipoId() {
        return tipoId;
    }

    public void setTipoId(UUID tipoId) {
        this.tipoId = tipoId;
    }

    public long getNumero() {
        return numero;
    }

    public void setNumero(long numero) {
        this.numero = numero;
    }

    public LocalDate getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(LocalDate dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public Instant getDataEmissaoTs() {
        return dataEmissaoTs;
    }

    public Short getAno() {
        return ano;
    }

    public void setAno(Short ano) {
        this.ano = ano;
    }

    public LocalDate getValidade() {
        return validade;
    }

    public void setValidade(LocalDate validade) {
        this.validade = validade;
    }

    public UUID getContribuinteId() {
        return contribuinteId;
    }

    public void setContribuinteId(UUID contribuinteId) {
        this.contribuinteId = contribuinteId;
    }

    public BigDecimal getAreaImovel() {
        return areaImovel;
    }

    public void setAreaImovel(BigDecimal areaImovel) {
        this.areaImovel = areaImovel;
    }

    public LocalDate getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(LocalDate dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public String getNumeroAlvara() {
        return numeroAlvara;
    }

    public void setNumeroAlvara(String numeroAlvara) {
        this.numeroAlvara = numeroAlvara;
    }

    public LocalDate getDataAlvara() {
        return dataAlvara;
    }

    public void setDataAlvara(LocalDate dataAlvara) {
        this.dataAlvara = dataAlvara;
    }

    public LocalDate getValidadeAlvara() {
        return validadeAlvara;
    }

    public void setValidadeAlvara(LocalDate validadeAlvara) {
        this.validadeAlvara = validadeAlvara;
    }

    public BigDecimal getValorBaseCalculo() {
        return valorBaseCalculo;
    }

    public void setValorBaseCalculo(BigDecimal valorBaseCalculo) {
        this.valorBaseCalculo = valorBaseCalculo;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getFrente() {
        return frente;
    }

    public void setFrente(BigDecimal frente) {
        this.frente = frente;
    }

    public BigDecimal getFundos() {
        return fundos;
    }

    public void setFundos(BigDecimal fundos) {
        this.fundos = fundos;
    }

    public BigDecimal getLadoEsquerdo() {
        return ladoEsquerdo;
    }

    public void setLadoEsquerdo(BigDecimal ladoEsquerdo) {
        this.ladoEsquerdo = ladoEsquerdo;
    }

    public BigDecimal getLadoDireito() {
        return ladoDireito;
    }

    public void setLadoDireito(BigDecimal ladoDireito) {
        this.ladoDireito = ladoDireito;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getCodigoVerificacao() {
        return codigoVerificacao;
    }

    public void setCodigoVerificacao(String codigoVerificacao) {
        this.codigoVerificacao = codigoVerificacao;
    }

    public SituacaoFiscalHabitese getSituacaoFiscal() {
        return situacaoFiscal;
    }

    public void setSituacaoFiscal(SituacaoFiscalHabitese situacaoFiscal) {
        this.situacaoFiscal = situacaoFiscal;
    }
}
