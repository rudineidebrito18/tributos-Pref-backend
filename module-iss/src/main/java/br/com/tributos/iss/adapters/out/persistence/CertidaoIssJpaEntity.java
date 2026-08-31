package br.com.tributos.iss.adapters.out.persistence;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import br.com.tributos.iss.domain.TipoCertidaoIss;

@Entity
@Table(name = "iss_certidao")
public class CertidaoIssJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoCertidaoIss tipo;

    @Column(name = "contribuinte_id", nullable = false)
    private UUID contribuinteId;

    @Column(nullable = false)
    private long numero;

    @Column(name = "codigo_verificacao", nullable = false, length = 32, unique = true)
    private String codigoVerificacao;

    @Column(name = "data_emissao", nullable = false)
    private Instant dataEmissao;

    @Column(nullable = false)
    private LocalDate validade;

    @Column(name = "situacao_cnd_id")
    private UUID situacaoCndId;

    @Column(name = "observacao")
    private String observacao;

    @Column(name = "avulsa", nullable = false)
    private boolean avulsa;

    protected CertidaoIssJpaEntity() {
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

    public TipoCertidaoIss getTipo() {
        return tipo;
    }

    public void setTipo(TipoCertidaoIss tipo) {
        this.tipo = tipo;
    }

    public UUID getContribuinteId() {
        return contribuinteId;
    }

    public void setContribuinteId(UUID contribuinteId) {
        this.contribuinteId = contribuinteId;
    }

    public long getNumero() {
        return numero;
    }

    public void setNumero(long numero) {
        this.numero = numero;
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

    public LocalDate getValidade() {
        return validade;
    }

    public void setValidade(LocalDate validade) {
        this.validade = validade;
    }

    public UUID getSituacaoCndId() {
        return situacaoCndId;
    }

    public void setSituacaoCndId(UUID situacaoCndId) {
        this.situacaoCndId = situacaoCndId;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public boolean isAvulsa() {
        return avulsa;
    }

    public void setAvulsa(boolean avulsa) {
        this.avulsa = avulsa;
    }
}
