package br.com.tributos.identity.adapters.out.persistence;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "mensagem_interna_destinatario")
public class MensagemInternaDestinatarioJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mensagem_id", nullable = false)
    private MensagemInternaJpaEntity mensagem;

    @Column(name = "destinatario_id", nullable = false)
    private UUID destinatarioId;

    @Column(name = "lida_em")
    private Instant lidaEm;

    @Column(name = "arquivada_em")
    private Instant arquivadaEm;

    protected MensagemInternaDestinatarioJpaEntity() {
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

    public MensagemInternaJpaEntity getMensagem() {
        return mensagem;
    }

    public void setMensagem(MensagemInternaJpaEntity mensagem) {
        this.mensagem = mensagem;
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
