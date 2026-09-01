package br.com.tributos.iptu.adapters.out.persistence;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "imovel_certidao_negativa")
public class CertidaoNegativaImovelJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "imovel_id", nullable = false)
    private UUID imovelId;

    @Column(nullable = false)
    private long numero;

    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @Column(nullable = false)
    private LocalDate validade;

    @Column(name = "codigo_verificacao", nullable = false, length = 32, unique = true)
    private String codigoVerificacao;

    @Column(name = "data_emissao_ts", nullable = false, insertable = false, updatable = false)
    private Instant dataEmissaoTs;

    @Column(name = "situacao_cnd_id")
    private UUID situacaoCndId;

    private String observacao;

    protected CertidaoNegativaImovelJpaEntity() {
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

    public LocalDate getValidade() {
        return validade;
    }

    public void setValidade(LocalDate validade) {
        this.validade = validade;
    }

    public String getCodigoVerificacao() {
        return codigoVerificacao;
    }

    public void setCodigoVerificacao(String codigoVerificacao) {
        this.codigoVerificacao = codigoVerificacao;
    }

    public Instant getDataEmissaoTs() {
        return dataEmissaoTs;
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
}
