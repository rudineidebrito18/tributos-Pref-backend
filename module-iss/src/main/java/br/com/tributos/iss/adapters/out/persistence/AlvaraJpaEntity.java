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

import br.com.tributos.iss.domain.SituacaoFiscalAlvara;

@Entity
@Table(name = "iss_alvara")
public class AlvaraJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private long numero;

    @Column(name = "tipo_alvara_id", nullable = false)
    private UUID tipoAlvaraId;

    @Column(name = "contribuinte_id", nullable = false)
    private UUID contribuinteId;

    @Column(name = "data_expedicao", nullable = false)
    private LocalDate dataExpedicao;

    @Column(nullable = false)
    private LocalDate validade;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao_fiscal", nullable = false, length = 20)
    private SituacaoFiscalAlvara situacaoFiscal;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal valor;

    @Column(name = "codigo_verificacao", nullable = false, length = 32, unique = true)
    private String codigoVerificacao;

    @Column(name = "data_emissao", nullable = false)
    private Instant dataEmissao;

    protected AlvaraJpaEntity() {
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

    public UUID getTipoAlvaraId() {
        return tipoAlvaraId;
    }

    public void setTipoAlvaraId(UUID tipoAlvaraId) {
        this.tipoAlvaraId = tipoAlvaraId;
    }

    public UUID getContribuinteId() {
        return contribuinteId;
    }

    public void setContribuinteId(UUID contribuinteId) {
        this.contribuinteId = contribuinteId;
    }

    public LocalDate getDataExpedicao() {
        return dataExpedicao;
    }

    public void setDataExpedicao(LocalDate dataExpedicao) {
        this.dataExpedicao = dataExpedicao;
    }

    public LocalDate getValidade() {
        return validade;
    }

    public void setValidade(LocalDate validade) {
        this.validade = validade;
    }

    public SituacaoFiscalAlvara getSituacaoFiscal() {
        return situacaoFiscal;
    }

    public void setSituacaoFiscal(SituacaoFiscalAlvara situacaoFiscal) {
        this.situacaoFiscal = situacaoFiscal;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getCodigoVerificacao() {
        return codigoVerificacao;
    }

    public void setCodigoVerificacao(String codigoVerificacao) {
        this.codigoVerificacao = codigoVerificacao;
    }

    public Instant getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Instant dataEmissao) {
        this.dataEmissao = dataEmissao;
    }
}
