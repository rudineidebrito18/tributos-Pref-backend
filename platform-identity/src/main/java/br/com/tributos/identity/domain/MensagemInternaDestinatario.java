package br.com.tributos.identity.domain;

import java.time.Instant;
import java.util.UUID;

public class MensagemInternaDestinatario {

    private UUID id;
    private UUID tenantId;
    private UUID mensagemId;
    private UUID destinatarioId;
    private Instant lidaEm;
    private Instant arquivadaEm;

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

    public UUID getMensagemId() {
        return mensagemId;
    }

    public void setMensagemId(UUID mensagemId) {
        this.mensagemId = mensagemId;
    }

    public UUID getDestinatarioId() {
        return destinatarioId;
    }

    public void setDestinatarioId(UUID destinatarioId) {
        this.destinatarioId = destinatarioId;
    }

    public Instant getLidaEm() {
        return lidaEm;
    }

    public void setLidaEm(Instant lidaEm) {
        this.lidaEm = lidaEm;
    }

    public Instant getArquivadaEm() {
        return arquivadaEm;
    }

    public void setArquivadaEm(Instant arquivadaEm) {
        this.arquivadaEm = arquivadaEm;
    }
}
