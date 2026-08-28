package br.com.tributos.iptu.adapters.out.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import br.com.tributos.iptu.domain.StatusLancamentoIptu;

@Entity
@Table(name = "iptu_lancamento")
public class LancamentoIptuJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "imovel_id", nullable = false)
    private UUID imovelId;

    @Column(nullable = false)
    private int exercicio;

    @Column(name = "valor_venal_calculado", nullable = false, precision = 14, scale = 2)
    private BigDecimal valorVenalCalculado;

    @Column(name = "aliquota_aplicada", nullable = false, precision = 10, scale = 6)
    private BigDecimal aliquotaAplicada;

    @Column(name = "valor_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "numero_parcelas", nullable = false)
    private int numeroParcelas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusLancamentoIptu status;

    @Column(name = "data_geracao", nullable = false, insertable = false, updatable = false)
    private Instant dataGeracao;

    protected LancamentoIptuJpaEntity() {
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

    public int getExercicio() {
        return exercicio;
    }

    public void setExercicio(int exercicio) {
        this.exercicio = exercicio;
    }

    public BigDecimal getValorVenalCalculado() {
        return valorVenalCalculado;
    }

    public void setValorVenalCalculado(BigDecimal valorVenalCalculado) {
        this.valorVenalCalculado = valorVenalCalculado;
    }

    public BigDecimal getAliquotaAplicada() {
        return aliquotaAplicada;
    }

    public void setAliquotaAplicada(BigDecimal aliquotaAplicada) {
        this.aliquotaAplicada = aliquotaAplicada;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public int getNumeroParcelas() {
        return numeroParcelas;
    }

    public void setNumeroParcelas(int numeroParcelas) {
        this.numeroParcelas = numeroParcelas;
    }

    public StatusLancamentoIptu getStatus() {
        return status;
    }

    public void setStatus(StatusLancamentoIptu status) {
        this.status = status;
    }

    public Instant getDataGeracao() {
        return dataGeracao;
    }
}
