package br.com.tributos.identity.domain;

import java.time.Instant;
import java.util.UUID;

public class ConfiguracaoPixBb {

    private UUID id;
    private UUID tenantId;
    private AmbientePixBb ambiente;
    private boolean ativo;
    private String clientId;
    private String clientSecret;
    private String developerApplicationKey;
    private String escopos;
    private String numeroConvenio;
    private String chavePix;
    private String indicadorCodigoBarras;
    private String certificadoPath;
    private String certificadoSenha;
    private String webhookUrl;
    private String webhookToken;
    private Instant criadoEm;
    private Instant atualizadoEm;

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

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
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

    public String getCertificadoSenha() {
        return certificadoSenha;
    }

    public void setCertificadoSenha(String certificadoSenha) {
        this.certificadoSenha = certificadoSenha;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public String getWebhookToken() {
        return webhookToken;
    }

    public void setWebhookToken(String webhookToken) {
        this.webhookToken = webhookToken;
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
