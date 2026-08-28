package br.com.tributos.cadastro.adapters.out.persistence;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "documento")
public class DocumentoJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "pessoa_id", nullable = false)
    private UUID pessoaId;

    @Column(nullable = false, length = 100)
    private String tipo;

    @Column(name = "nome_arquivo", nullable = false)
    private String nomeArquivo;

    @Column(name = "conteudo_tipo", nullable = false, length = 100)
    private String conteudoTipo;

    @Column(name = "tamanho_bytes", nullable = false)
    private long tamanhoBytes;

    @Column(name = "storage_chave", nullable = false, length = 500)
    private String storageChave;

    @Column(nullable = false)
    private boolean compartilhado;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    protected DocumentoJpaEntity() {
    }

    public DocumentoJpaEntity(
        UUID id, UUID tenantId, UUID pessoaId, String tipo, String nomeArquivo,
        String conteudoTipo, long tamanhoBytes, String storageChave, boolean compartilhado, Instant criadoEm
    ) {
        this.id = id;
        this.tenantId = tenantId;
        this.pessoaId = pessoaId;
        this.tipo = tipo;
        this.nomeArquivo = nomeArquivo;
        this.conteudoTipo = conteudoTipo;
        this.tamanhoBytes = tamanhoBytes;
        this.storageChave = storageChave;
        this.compartilhado = compartilhado;
        this.criadoEm = criadoEm;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public UUID getPessoaId() {
        return pessoaId;
    }

    public String getTipo() {
        return tipo;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public String getConteudoTipo() {
        return conteudoTipo;
    }

    public long getTamanhoBytes() {
        return tamanhoBytes;
    }

    public String getStorageChave() {
        return storageChave;
    }

    public boolean isCompartilhado() {
        return compartilhado;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
