package br.com.tributos.identity.adapters.out.persistence;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import br.com.tributos.identity.domain.AmbientePixBb;

@Entity
@Table(name = "configuracao_pix_bb")
public class ConfiguracaoPixBbJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AmbientePixBb ambiente;

    @Column(nullable = false)
    private boolean ativo;

    @Column(name = "client_id", nullable = false, columnDefinition = "TEXT")
    private String clientId;

    @Column(name = "client_secret_cifrado", nullable = false, columnDefinition = "TEXT")
    private String clientSecretCifrado;

    @Column(name = "developer_application_key", nullable = false, columnDefinition = "TEXT")
    private String developerApplicationKey;

    @Column(nullable = false, length = 500)
    private String escopos;

    @Column(name = "numero_convenio", nullable = false, length = 6)
    private String numeroConvenio;

    @Column(name = "chave_pix", nullable = false, length = 77)
    private String chavePix;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "indicador_codigo_barras", nullable = false, length = 1)
    private String indicadorCodigoBarras;

    @Column(name = "certificado_path", columnDefinition = "TEXT")
    private String certificadoPath;

    @Column(name = "certificado_senha_cifrada", columnDefinition = "TEXT")
    private String certificadoSenhaCifrada;

    @Column(name = "webhook_url", columnDefinition = "TEXT")
    private String webhookUrl;

    @Column(name = "webhook_token_cifrado", columnDefinition = "TEXT")
    private String webhookTokenCifrado;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected ConfiguracaoPixBbJpaEntity() {
    }

    @PrePersist
    void prePersist() {
        Instant agora = Instant.now();
        if (criadoEm == null) {
            criadoEm = agora;
        }
        atualizadoEm = agora;
    }

    @PreUpdate
    void preUpdate() {
        atualizadoEm = Instant.now();
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

    public AmbientePixBb getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(AmbientePixBb ambiente) {
        this.ambiente = ambiente;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecretCifrado() {
        return clientSecretCifrado;
    }

    public void setClientSecretCifrado(String clientSecretCifrado) {
        this.clientSecretCifrado = clientSecretCifrado;
    }

    public String getDeveloperApplicationKey() {
        return developerApplicationKey;
    }

    public void setDeveloperApplicationKey(String developerApplicationKey) {
        this.developerApplicationKey = developerApplicationKey;
    }

    public String getEscopos() {
        return escopos;
    }

    public void setEscopos(String escopos) {
        this.escopos = escopos;
    }

    public String getNumeroConvenio() {
        return numeroConvenio;
    }

    public void setNumeroConvenio(String numeroConvenio) {
        this.numeroConvenio = numeroConvenio;
    }

    public String getChavePix() {
        return chavePix;
    }

    public void setChavePix(String chavePix) {
        this.chavePix = chavePix;
    }

    public String getIndicadorCodigoBarras() {
        return indicadorCodigoBarras;
    }

    public void setIndicadorCodigoBarras(String indicadorCodigoBarras) {
        this.indicadorCodigoBarras = indicadorCodigoBarras;
    }

    public String getCertificadoPath() {
        return certificadoPath;
    }

    public void setCertificadoPath(String certificadoPath) {
        this.certificadoPath = certificadoPath;
    }

    public String getCertificadoSenhaCifrada() {
        return certificadoSenhaCifrada;
    }

    public void setCertificadoSenhaCifrada(String certificadoSenhaCifrada) {
        this.certificadoSenhaCifrada = certificadoSenhaCifrada;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public String getWebhookTokenCifrado() {
        return webhookTokenCifrado;
    }

    public void setWebhookTokenCifrado(String webhookTokenCifrado) {
        this.webhookTokenCifrado = webhookTokenCifrado;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Instant criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(Instant atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }
}
