package br.com.tributos.iss.adapters.out.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "iss_atividade_servico")
public class AtividadeServicoJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "atividade_id", nullable = false)
    private UUID atividadeId;

    @Column(name = "servico_id")
    private UUID servicoId;

    @Column(name = "local_incidencia_id", nullable = false)
    private UUID localIncidenciaId;

    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal aliquota;

    @Column(nullable = false)
    private boolean tributavel;

    @Column(nullable = false)
    private boolean imune;

    @Column(nullable = false)
    private boolean deducao;

    @Column(name = "substituto_tributario", nullable = false)
    private boolean substitutoTributario;

    @Column(name = "retencao_fonte", nullable = false)
    private boolean retencaoFonte;

    @Column(name = "regime_especial", length = 200)
    private String regimeEspecial;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Column(name = "criado_em", nullable = false, updatable = false, insertable = false)
    private Instant criadoEm;

    protected AtividadeServicoJpaEntity() {
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

    public UUID getAtividadeId() {
        return atividadeId;
    }

    public void setAtividadeId(UUID atividadeId) {
        this.atividadeId = atividadeId;
    }

    public UUID getServicoId() {
        return servicoId;
    }

    public void setServicoId(UUID servicoId) {
        this.servicoId = servicoId;
    }

    public UUID getLocalIncidenciaId() {
        return localIncidenciaId;
    }

    public void setLocalIncidenciaId(UUID localIncidenciaId) {
        this.localIncidenciaId = localIncidenciaId;
    }

    public BigDecimal getAliquota() {
        return aliquota;
    }

    public void setAliquota(BigDecimal aliquota) {
        this.aliquota = aliquota;
    }

    public boolean isTributavel() {
        return tributavel;
    }

    public void setTributavel(boolean tributavel) {
        this.tributavel = tributavel;
    }

    public boolean isImune() {
        return imune;
    }

    public void setImune(boolean imune) {
        this.imune = imune;
    }

    public boolean isDeducao() {
        return deducao;
    }

    public void setDeducao(boolean deducao) {
        this.deducao = deducao;
    }

    public boolean isSubstitutoTributario() {
        return substitutoTributario;
    }

    public void setSubstitutoTributario(boolean substitutoTributario) {
        this.substitutoTributario = substitutoTributario;
    }

    public boolean isRetencaoFonte() {
        return retencaoFonte;
    }

    public void setRetencaoFonte(boolean retencaoFonte) {
        this.retencaoFonte = retencaoFonte;
    }

    public String getRegimeEspecial() {
        return regimeEspecial;
    }

    public void setRegimeEspecial(String regimeEspecial) {
        this.regimeEspecial = regimeEspecial;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
