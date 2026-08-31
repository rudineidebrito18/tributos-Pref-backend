package br.com.tributos.itbi.adapters.out.persistence;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "itbi_tipo_guia")
public class TipoGuiaItbiJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal aliquota;

    @Column(nullable = false)
    private boolean ativo;

    @Column(name = "tipo_calculo_id")
    private UUID tipoCalculoId;

    @Column(name = "permite_desconto", nullable = false)
    private boolean permiteDesconto;

    @Column(name = "habilita_calculo_valor", nullable = false)
    private boolean habilitaCalculoValor;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal valor = BigDecimal.ZERO;

    @Column(name = "valor_parcela", precision = 14, scale = 2)
    private BigDecimal valorParcela;

    @Column(length = 200)
    private String secretaria;

    @Column(length = 200)
    private String cargo;

    @Column(name = "assinatura_documento_id")
    private UUID assinaturaDocumentoId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public BigDecimal getAliquota() { return aliquota; }
    public void setAliquota(BigDecimal aliquota) { this.aliquota = aliquota; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public UUID getTipoCalculoId() { return tipoCalculoId; }
    public void setTipoCalculoId(UUID tipoCalculoId) { this.tipoCalculoId = tipoCalculoId; }
    public boolean isPermiteDesconto() { return permiteDesconto; }
    public void setPermiteDesconto(boolean permiteDesconto) { this.permiteDesconto = permiteDesconto; }
    public boolean isHabilitaCalculoValor() { return habilitaCalculoValor; }
    public void setHabilitaCalculoValor(boolean habilitaCalculoValor) { this.habilitaCalculoValor = habilitaCalculoValor; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public BigDecimal getValorParcela() { return valorParcela; }
    public void setValorParcela(BigDecimal valorParcela) { this.valorParcela = valorParcela; }
    public String getSecretaria() { return secretaria; }
    public void setSecretaria(String secretaria) { this.secretaria = secretaria; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public UUID getAssinaturaDocumentoId() { return assinaturaDocumentoId; }
    public void setAssinaturaDocumentoId(UUID assinaturaDocumentoId) { this.assinaturaDocumentoId = assinaturaDocumentoId; }
}
