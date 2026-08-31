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

import br.com.tributos.iss.domain.BaseVencimentoAlvara;

@Entity
@Table(name = "iss_tipo_alvara")
public class TipoAlvaraJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "valor_base", nullable = false, precision = 14, scale = 2)
    private BigDecimal valorBase;

    @Column(name = "dias_validade", nullable = false)
    private int diasValidade;

    @Column(nullable = false)
    private boolean ativo;

    @Column(name = "ano_vigencia")
    private Short anoVigencia;

    @Column(name = "identificacao_modelo_documento", length = 100)
    private String identificacaoModeloDocumento;

    @Column(name = "permite_valor_dinamico", nullable = false)
    private boolean permiteValorDinamico;

    @Column(name = "permite_calculo_valor", nullable = false)
    private boolean permiteCalculoValor;

    @Column(name = "unidade_medida_descritivo", length = 100)
    private String unidadeMedidaDescritivo;

    @Column(name = "habilitar_validade", nullable = false)
    private boolean habilitarValidade;

    @Column(name = "habilitar_calculo_vencimento", nullable = false)
    private boolean habilitarCalculoVencimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "base_vencimento", length = 5)
    private BaseVencimentoAlvara baseVencimento;

    @Column(name = "dias_meses_vencimento")
    private Integer diasMesesVencimento;

    @Column(length = 200)
    private String titulo;

    @Column(length = 200)
    private String secretaria;

    @Column(length = 200)
    private String cargo;

    @Column(name = "assinatura_documento_id")
    private UUID assinaturaDocumentoId;

    protected TipoAlvaraJpaEntity() {
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getValorBase() {
        return valorBase;
    }

    public void setValorBase(BigDecimal valorBase) {
        this.valorBase = valorBase;
    }

    public int getDiasValidade() {
        return diasValidade;
    }

    public void setDiasValidade(int diasValidade) {
        this.diasValidade = diasValidade;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Short getAnoVigencia() {
        return anoVigencia;
    }

    public void setAnoVigencia(Short anoVigencia) {
        this.anoVigencia = anoVigencia;
    }

    public String getIdentificacaoModeloDocumento() {
        return identificacaoModeloDocumento;
    }

    public void setIdentificacaoModeloDocumento(String identificacaoModeloDocumento) {
        this.identificacaoModeloDocumento = identificacaoModeloDocumento;
    }

    public boolean isPermiteValorDinamico() {
        return permiteValorDinamico;
    }

    public void setPermiteValorDinamico(boolean permiteValorDinamico) {
        this.permiteValorDinamico = permiteValorDinamico;
    }

    public boolean isPermiteCalculoValor() {
        return permiteCalculoValor;
    }

    public void setPermiteCalculoValor(boolean permiteCalculoValor) {
        this.permiteCalculoValor = permiteCalculoValor;
    }

    public String getUnidadeMedidaDescritivo() {
        return unidadeMedidaDescritivo;
    }

    public void setUnidadeMedidaDescritivo(String unidadeMedidaDescritivo) {
        this.unidadeMedidaDescritivo = unidadeMedidaDescritivo;
    }

    public boolean isHabilitarValidade() {
        return habilitarValidade;
    }

    public void setHabilitarValidade(boolean habilitarValidade) {
        this.habilitarValidade = habilitarValidade;
    }

    public boolean isHabilitarCalculoVencimento() {
        return habilitarCalculoVencimento;
    }

    public void setHabilitarCalculoVencimento(boolean habilitarCalculoVencimento) {
        this.habilitarCalculoVencimento = habilitarCalculoVencimento;
    }

    public BaseVencimentoAlvara getBaseVencimento() {
        return baseVencimento;
    }

    public void setBaseVencimento(BaseVencimentoAlvara baseVencimento) {
        this.baseVencimento = baseVencimento;
    }

    public Integer getDiasMesesVencimento() {
        return diasMesesVencimento;
    }

    public void setDiasMesesVencimento(Integer diasMesesVencimento) {
        this.diasMesesVencimento = diasMesesVencimento;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSecretaria() {
        return secretaria;
    }

    public void setSecretaria(String secretaria) {
        this.secretaria = secretaria;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public UUID getAssinaturaDocumentoId() {
        return assinaturaDocumentoId;
    }

    public void setAssinaturaDocumentoId(UUID assinaturaDocumentoId) {
        this.assinaturaDocumentoId = assinaturaDocumentoId;
    }
}
