package br.com.tributos.iss.adapters.out.persistence;

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

import br.com.tributos.iss.domain.StatusNotaFiscal;

@Entity
@Table(name = "iss_nota_fiscal")
public class NotaFiscalJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private long numero;

    @Column(nullable = false, length = 10)
    private String serie;

    @Column(name = "contribuinte_id", nullable = false)
    private UUID contribuinteId;

    @Column(name = "tomador_id", nullable = false)
    private UUID tomadorId;

    @Column(name = "servico_id", nullable = false)
    private UUID servicoId;

    @Column(nullable = false)
    private LocalDate competencia;

    @Column(name = "valor_servico", nullable = false, precision = 14, scale = 2)
    private BigDecimal valorServico;

    @Column(name = "valor_deducoes", nullable = false, precision = 14, scale = 2)
    private BigDecimal valorDeducoes;

    @Column(name = "base_calculo", nullable = false, precision = 14, scale = 2)
    private BigDecimal baseCalculo;

    @Column(name = "aliquota_aplicada", nullable = false, precision = 10, scale = 6)
    private BigDecimal aliquotaAplicada;

    @Column(name = "valor_iss", nullable = false, precision = 14, scale = 2)
    private BigDecimal valorIss;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusNotaFiscal status;

    @Column(name = "nota_substituta_id")
    private UUID notaSubstitutaId;

    @Column(name = "motivo_cancelamento")
    private String motivoCancelamento;

    @Column(name = "data_emissao", nullable = false)
    private Instant dataEmissao;

    protected NotaFiscalJpaEntity() {
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

    public long getNumero() {
        return numero;
    }

    public void setNumero(long numero) {
        this.numero = numero;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public UUID getContribuinteId() {
        return contribuinteId;
    }

    public void setContribuinteId(UUID contribuinteId) {
        this.contribuinteId = contribuinteId;
    }

    public UUID getTomadorId() {
        return tomadorId;
    }

    public void setTomadorId(UUID tomadorId) {
        this.tomadorId = tomadorId;
    }

    public UUID getServicoId() {
        return servicoId;
    }

    public void setServicoId(UUID servicoId) {
        this.servicoId = servicoId;
    }

    public LocalDate getCompetencia() {
        return competencia;
    }

    public void setCompetencia(LocalDate competencia) {
        this.competencia = competencia;
    }

    public BigDecimal getValorServico() {
        return valorServico;
    }

    public void setValorServico(BigDecimal valorServico) {
        this.valorServico = valorServico;
    }

    public BigDecimal getValorDeducoes() {
        return valorDeducoes;
    }

    public void setValorDeducoes(BigDecimal valorDeducoes) {
        this.valorDeducoes = valorDeducoes;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public BigDecimal getAliquotaAplicada() {
        return aliquotaAplicada;
    }

    public void setAliquotaAplicada(BigDecimal aliquotaAplicada) {
        this.aliquotaAplicada = aliquotaAplicada;
    }

    public BigDecimal getValorIss() {
        return valorIss;
    }

    public void setValorIss(BigDecimal valorIss) {
        this.valorIss = valorIss;
    }

    public StatusNotaFiscal getStatus() {
        return status;
    }

    public void setStatus(StatusNotaFiscal status) {
        this.status = status;
    }

    public UUID getNotaSubstitutaId() {
        return notaSubstitutaId;
    }

    public void setNotaSubstitutaId(UUID notaSubstitutaId) {
        this.notaSubstitutaId = notaSubstitutaId;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public Instant getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Instant dataEmissao) {
        this.dataEmissao = dataEmissao;
    }
}
