package br.com.tributos.identity.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MensagemInterna {

    private UUID id;
    private UUID tenantId;
    private UUID remetenteId;
    private String assunto;
    private String corpo;
    private Instant criadoEm;
    private List<MensagemInternaDestinatario> destinatarios = new ArrayList<>();

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

    public UUID getRemetenteId() {
        return remetenteId;
    }

    public void setRemetenteId(UUID remetenteId) {
        this.remetenteId = remetenteId;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getCorpo() {
        return corpo;
    }

    public void setCorpo(String corpo) {
        this.corpo = corpo;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Instant criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<MensagemInternaDestinatario> getDestinatarios() {
        return destinatarios;
    }

    public void setDestinatarios(List<MensagemInternaDestinatario> destinatarios) {
        this.destinatarios = destinatarios;
    }
}
