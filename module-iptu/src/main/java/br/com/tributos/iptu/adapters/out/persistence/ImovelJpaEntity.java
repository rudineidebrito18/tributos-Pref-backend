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

import br.com.tributos.iptu.domain.SituacaoImovel;

@Entity
@Table(name = "imovel")
public class ImovelJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "numero_cadastro", nullable = false)
    private long numeroCadastro;

    @Column(name = "codigo_legado", length = 50)
    private String codigoLegado;

    @Column(name = "proprietario_id", nullable = false)
    private UUID proprietarioId;

    @Column(name = "tipo_id", nullable = false)
    private UUID tipoId;

    @Column(name = "endereco_id")
    private UUID enderecoId;

    @Column(name = "area_terreno", precision = 14, scale = 4)
    private BigDecimal areaTerreno;

    @Column(name = "area_construida", precision = 14, scale = 4)
    private BigDecimal areaConstruida;

    @Column(name = "destinacao_id")
    private UUID destinacaoId;

    @Column(name = "tipo_edificacao_id")
    private UUID tipoEdificacaoId;

    @Column(name = "tipo_limitacao_id")
    private UUID tipoLimitacaoId;

    @Column(name = "zona_fiscal_id")
    private UUID zonaFiscalId;

    @Column(name = "valor_venal_terreno", nullable = false, precision = 14, scale = 2)
    private BigDecimal valorVenalTerreno;

    @Column(name = "valor_venal_construcao", nullable = false, precision = 14, scale = 2)
    private BigDecimal valorVenalConstrucao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SituacaoImovel situacao;

    @Column(name = "criado_em", nullable = false, updatable = false, insertable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false, insertable = false)
    private Instant atualizadoEm;

    protected ImovelJpaEntity() {
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

    public long getNumeroCadastro() {
        return numeroCadastro;
    }

    public void setNumeroCadastro(long numeroCadastro) {
        this.numeroCadastro = numeroCadastro;
    }

    public String getCodigoLegado() {
        return codigoLegado;
    }

    public void setCodigoLegado(String codigoLegado) {
        this.codigoLegado = codigoLegado;
    }

    public UUID getProprietarioId() {
        return proprietarioId;
    }

    public void setProprietarioId(UUID proprietarioId) {
        this.proprietarioId = proprietarioId;
    }

    public UUID getTipoId() {
        return tipoId;
    }

    public void setTipoId(UUID tipoId) {
        this.tipoId = tipoId;
    }

    public UUID getEnderecoId() {
        return enderecoId;
    }

    public void setEnderecoId(UUID enderecoId) {
        this.enderecoId = enderecoId;
    }

    public BigDecimal getAreaTerreno() {
        return areaTerreno;
    }

    public void setAreaTerreno(BigDecimal areaTerreno) {
        this.areaTerreno = areaTerreno;
    }

    public BigDecimal getAreaConstruida() {
        return areaConstruida;
    }

    public void setAreaConstruida(BigDecimal areaConstruida) {
        this.areaConstruida = areaConstruida;
    }

    public UUID getDestinacaoId() {
        return destinacaoId;
    }

    public void setDestinacaoId(UUID destinacaoId) {
        this.destinacaoId = destinacaoId;
    }

    public UUID getTipoEdificacaoId() {
        return tipoEdificacaoId;
    }

    public void setTipoEdificacaoId(UUID tipoEdificacaoId) {
        this.tipoEdificacaoId = tipoEdificacaoId;
    }

    public UUID getTipoLimitacaoId() {
        return tipoLimitacaoId;
    }

    public void setTipoLimitacaoId(UUID tipoLimitacaoId) {
        this.tipoLimitacaoId = tipoLimitacaoId;
    }

    public UUID getZonaFiscalId() {
        return zonaFiscalId;
    }

    public void setZonaFiscalId(UUID zonaFiscalId) {
        this.zonaFiscalId = zonaFiscalId;
    }

    public BigDecimal getValorVenalTerreno() {
        return valorVenalTerreno;
    }

    public void setValorVenalTerreno(BigDecimal valorVenalTerreno) {
        this.valorVenalTerreno = valorVenalTerreno;
    }

    public BigDecimal getValorVenalConstrucao() {
        return valorVenalConstrucao;
    }

    public void setValorVenalConstrucao(BigDecimal valorVenalConstrucao) {
        this.valorVenalConstrucao = valorVenalConstrucao;
    }

    public SituacaoImovel getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoImovel situacao) {
        this.situacao = situacao;
    }
}
