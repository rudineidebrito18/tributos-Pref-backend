package br.com.tributos.iss.adapters.out.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "iss_valor_alvara")
public class ValorAlvaraJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "tipo_alvara_id", nullable = false)
    private UUID tipoAlvaraId;

    @Column(name = "ano_vigencia", nullable = false)
    private short anoVigencia;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal valor;

    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected ValorAlvaraJpaEntity() {
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

    public UUID getTipoAlvaraId() {
        return tipoAlvaraId;
    }

    public void setTipoAlvaraId(UUID tipoAlvaraId) {
        this.tipoAlvaraId = tipoAlvaraId;
    }

    public short getAnoVigencia() {
        return anoVigencia;
    }

    public void setAnoVigencia(short anoVigencia) {
        this.anoVigencia = anoVigencia;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(Instant atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }
}
