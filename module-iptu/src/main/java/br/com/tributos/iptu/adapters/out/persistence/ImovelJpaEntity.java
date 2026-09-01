package br.com.tributos.iptu.adapters.out.persistence;

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

    @Column(name = "ano_exercicio")
    private Short anoExercicio;

    @Column(name = "data_inclusao")
    private LocalDate dataInclusao;

    @Column(name = "area_total", precision = 14, scale = 4)
    private BigDecimal areaTotal;

    @Column(precision = 10, scale = 2)
    private BigDecimal frente;

    @Column(precision = 10, scale = 2)
    private BigDecimal fundos;

    @Column(name = "lado_esquerdo", precision = 10, scale = 2)
    private BigDecimal ladoEsquerdo;

    @Column(name = "lado_direito", precision = 10, scale = 2)
    private BigDecimal ladoDireito;

    @Column(length = 20)
    private String quadra;

    @Column(length = 20)
    private String lote;

    @Column(length = 100)
    private String loteamento;

    @Column(length = 100)
    private String edificio;

    @Column(length = 20)
    private String bloco;

    @Column(length = 20)
    private String sala;

    @Column(length = 20)
    private String apartamento;

    @Column(name = "bairro_iptu_id")
    private UUID bairroIptuId;

    @Column(name = "logradouro_iptu_id")
    private UUID logradouroIptuId;

    @Column(name = "valor_venal_unidade", nullable = false, precision = 14, scale = 2)
    private BigDecimal valorVenalUnidade;

    @Column(name = "valor_avaliacao", nullable = false, precision = 14, scale = 2)
    private BigDecimal valorAvaliacao;

    @Column(name = "endereco_correspondencia_id")
    private UUID enderecoCorrespondenciaId;

    @Column(name = "observacao")
    private String observacao;

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

    public Short getAnoExercicio() {
        return anoExercicio;
    }

    public void setAnoExercicio(Short anoExercicio) {
        this.anoExercicio = anoExercicio;
    }

    public LocalDate getDataInclusao() {
        return dataInclusao;
    }

    public void setDataInclusao(LocalDate dataInclusao) {
        this.dataInclusao = dataInclusao;
    }

    public BigDecimal getAreaTotal() {
        return areaTotal;
    }

    public void setAreaTotal(BigDecimal areaTotal) {
        this.areaTotal = areaTotal;
    }

    public BigDecimal getFrente() {
        return frente;
    }

    public void setFrente(BigDecimal frente) {
        this.frente = frente;
    }

    public BigDecimal getFundos() {
        return fundos;
    }

    public void setFundos(BigDecimal fundos) {
        this.fundos = fundos;
    }

    public BigDecimal getLadoEsquerdo() {
        return ladoEsquerdo;
    }

    public void setLadoEsquerdo(BigDecimal ladoEsquerdo) {
        this.ladoEsquerdo = ladoEsquerdo;
    }

    public BigDecimal getLadoDireito() {
        return ladoDireito;
    }

    public void setLadoDireito(BigDecimal ladoDireito) {
        this.ladoDireito = ladoDireito;
    }

    public String getQuadra() {
        return quadra;
    }

    public void setQuadra(String quadra) {
        this.quadra = quadra;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getLoteamento() {
        return loteamento;
    }

    public void setLoteamento(String loteamento) {
        this.loteamento = loteamento;
    }

    public String getEdificio() {
        return edificio;
    }

    public void setEdificio(String edificio) {
        this.edificio = edificio;
    }

    public String getBloco() {
        return bloco;
    }

    public void setBloco(String bloco) {
        this.bloco = bloco;
    }

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }

    public String getApartamento() {
        return apartamento;
    }

    public void setApartamento(String apartamento) {
        this.apartamento = apartamento;
    }

    public UUID getBairroIptuId() {
        return bairroIptuId;
    }

    public void setBairroIptuId(UUID bairroIptuId) {
        this.bairroIptuId = bairroIptuId;
    }

    public UUID getLogradouroIptuId() {
        return logradouroIptuId;
    }

    public void setLogradouroIptuId(UUID logradouroIptuId) {
        this.logradouroIptuId = logradouroIptuId;
    }

    public BigDecimal getValorVenalUnidade() {
        return valorVenalUnidade;
    }

    public void setValorVenalUnidade(BigDecimal valorVenalUnidade) {
        this.valorVenalUnidade = valorVenalUnidade;
    }

    public BigDecimal getValorAvaliacao() {
        return valorAvaliacao;
    }

    public void setValorAvaliacao(BigDecimal valorAvaliacao) {
        this.valorAvaliacao = valorAvaliacao;
    }

    public UUID getEnderecoCorrespondenciaId() {
        return enderecoCorrespondenciaId;
    }

    public void setEnderecoCorrespondenciaId(UUID enderecoCorrespondenciaId) {
        this.enderecoCorrespondenciaId = enderecoCorrespondenciaId;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
