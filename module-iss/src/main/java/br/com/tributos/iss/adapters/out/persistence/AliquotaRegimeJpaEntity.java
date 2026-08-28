package br.com.tributos.iss.adapters.out.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "iss_aliquota_regime")
public class AliquotaRegimeJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "regime_id", nullable = false)
    private UUID regimeId;

    @Column(name = "faixa_receita_min", nullable = false, precision = 14, scale = 2)
    private BigDecimal faixaReceitaMin;

    @Column(name = "faixa_receita_max", precision = 14, scale = 2)
    private BigDecimal faixaReceitaMax;

    @Column(name = "aliquota_nominal", nullable = false, precision = 8, scale = 4)
    private BigDecimal aliquotaNominal;

    @Column(name = "parcela_deduzir", nullable = false, precision = 14, scale = 2)
    private BigDecimal parcelaDeduzir;

    @Column(name = "percentual_iss", nullable = false, precision = 8, scale = 4)
    private BigDecimal percentualIss;

    @Column(name = "competencia_vigencia", nullable = false)
    private LocalDate competenciaVigencia;

    @Column(name = "anexo_simples", length = 10)
    private String anexoSimples;

    @Column(name = "criado_em", nullable = false, updatable = false, insertable = false)
    private Instant criadoEm;

    protected AliquotaRegimeJpaEntity() {
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

    public UUID getRegimeId() {
        return regimeId;
    }

    public void setRegimeId(UUID regimeId) {
        this.regimeId = regimeId;
    }

    public BigDecimal getFaixaReceitaMin() {
        return faixaReceitaMin;
    }

    public void setFaixaReceitaMin(BigDecimal faixaReceitaMin) {
        this.faixaReceitaMin = faixaReceitaMin;
    }

    public BigDecimal getFaixaReceitaMax() {
        return faixaReceitaMax;
    }

    public void setFaixaReceitaMax(BigDecimal faixaReceitaMax) {
        this.faixaReceitaMax = faixaReceitaMax;
    }

    public BigDecimal getAliquotaNominal() {
        return aliquotaNominal;
    }

    public void setAliquotaNominal(BigDecimal aliquotaNominal) {
        this.aliquotaNominal = aliquotaNominal;
    }

    public BigDecimal getParcelaDeduzir() {
        return parcelaDeduzir;
    }

    public void setParcelaDeduzir(BigDecimal parcelaDeduzir) {
        this.parcelaDeduzir = parcelaDeduzir;
    }

    public BigDecimal getPercentualIss() {
        return percentualIss;
    }

    public void setPercentualIss(BigDecimal percentualIss) {
        this.percentualIss = percentualIss;
    }

    public LocalDate getCompetenciaVigencia() {
        return competenciaVigencia;
    }

    public void setCompetenciaVigencia(LocalDate competenciaVigencia) {
        this.competenciaVigencia = competenciaVigencia;
    }

    public String getAnexoSimples() {
        return anexoSimples;
    }

    public void setAnexoSimples(String anexoSimples) {
        this.anexoSimples = anexoSimples;
    }
}
