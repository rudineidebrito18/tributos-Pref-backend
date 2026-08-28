package br.com.tributos.iss.adapters.out.persistence;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "iss_contribuinte")
public class ContribuinteJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "pessoa_id", nullable = false)
    private UUID pessoaId;

    @Column(name = "inscricao_municipal", nullable = false, length = 20)
    private String inscricaoMunicipal;

    @Column(name = "tipo_contribuinte_id", nullable = false)
    private UUID tipoContribuinteId;

    @Column(name = "situacao_cadastral_id", nullable = false)
    private UUID situacaoCadastralId;

    @Column(name = "status_credenciamento_id", nullable = false)
    private UUID statusCredenciamentoId;

    @Column(name = "regime_tributario_id", nullable = false)
    private UUID regimeTributarioId;

    @Column(name = "nome_contador")
    private String nomeContador;

    @Column(name = "email_contador")
    private String emailContador;

    @Column(name = "criado_em", nullable = false, updatable = false, insertable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false, insertable = false)
    private Instant atualizadoEm;

    protected ContribuinteJpaEntity() {
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

    public UUID getPessoaId() {
        return pessoaId;
    }

    public void setPessoaId(UUID pessoaId) {
        this.pessoaId = pessoaId;
    }

    public String getInscricaoMunicipal() {
        return inscricaoMunicipal;
    }

    public void setInscricaoMunicipal(String inscricaoMunicipal) {
        this.inscricaoMunicipal = inscricaoMunicipal;
    }

    public UUID getTipoContribuinteId() {
        return tipoContribuinteId;
    }

    public void setTipoContribuinteId(UUID tipoContribuinteId) {
        this.tipoContribuinteId = tipoContribuinteId;
    }

    public UUID getSituacaoCadastralId() {
        return situacaoCadastralId;
    }

    public void setSituacaoCadastralId(UUID situacaoCadastralId) {
        this.situacaoCadastralId = situacaoCadastralId;
    }

    public UUID getStatusCredenciamentoId() {
        return statusCredenciamentoId;
    }

    public void setStatusCredenciamentoId(UUID statusCredenciamentoId) {
        this.statusCredenciamentoId = statusCredenciamentoId;
    }

    public UUID getRegimeTributarioId() {
        return regimeTributarioId;
    }

    public void setRegimeTributarioId(UUID regimeTributarioId) {
        this.regimeTributarioId = regimeTributarioId;
    }

    public String getNomeContador() {
        return nomeContador;
    }

    public void setNomeContador(String nomeContador) {
        this.nomeContador = nomeContador;
    }

    public String getEmailContador() {
        return emailContador;
    }

    public void setEmailContador(String emailContador) {
        this.emailContador = emailContador;
    }
}
