package br.com.tributos.financeiro.adapters.out.persistence;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import br.com.tributos.financeiro.domain.OrigemConciliacaoPix;

@Entity
@Table(name = "pix_conciliacao_log")
class PixConciliacaoLogJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "guia_id")
    private UUID guiaId;

    @Column(length = 100)
    private String txid;

    @Column(name = "end_to_end_id", length = 100)
    private String endToEndId;

    @Column(name = "status_anterior", length = 40)
    private String statusAnterior;

    @Column(name = "status_novo", length = 40)
    private String statusNovo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrigemConciliacaoPix origem;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload_bruto", columnDefinition = "jsonb")
    private String payloadBruto;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    UUID getId() {
        return id;
    }

    void setId(UUID id) {
        this.id = id;
    }

    UUID getTenantId() {
        return tenantId;
    }

    void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    UUID getGuiaId() {
        return guiaId;
    }

    void setGuiaId(UUID guiaId) {
        this.guiaId = guiaId;
    }

    String getTxid() {
        return txid;
    }

    void setTxid(String txid) {
        this.txid = txid;
    }

    String getEndToEndId() {
        return endToEndId;
    }

    void setEndToEndId(String endToEndId) {
        this.endToEndId = endToEndId;
    }

    String getStatusAnterior() {
        return statusAnterior;
    }

    void setStatusAnterior(String statusAnterior) {
        this.statusAnterior = statusAnterior;
    }

    String getStatusNovo() {
        return statusNovo;
    }

    void setStatusNovo(String statusNovo) {
        this.statusNovo = statusNovo;
    }

    OrigemConciliacaoPix getOrigem() {
        return origem;
    }

    void setOrigem(OrigemConciliacaoPix origem) {
        this.origem = origem;
    }

    String getPayloadBruto() {
        return payloadBruto;
    }

    void setPayloadBruto(String payloadBruto) {
        this.payloadBruto = payloadBruto;
    }

    Instant getCriadoEm() {
        return criadoEm;
    }

    void setCriadoEm(Instant criadoEm) {
        this.criadoEm = criadoEm;
    }
}
