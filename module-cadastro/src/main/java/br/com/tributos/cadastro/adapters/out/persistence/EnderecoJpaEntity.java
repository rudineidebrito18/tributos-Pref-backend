package br.com.tributos.cadastro.adapters.out.persistence;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "endereco")
public class EnderecoJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_id", nullable = false)
    private PessoaJpaEntity pessoa;

    @Column(name = "logradouro_texto")
    private String logradouroTexto;

    private String numero;

    private String complemento;

    @Column(name = "bairro_texto")
    private String bairroTexto;

    @Column(name = "cidade_id")
    private UUID cidadeId;

    private String cep;

    @Column(nullable = false)
    private boolean principal;

    protected EnderecoJpaEntity() {
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

    public PessoaJpaEntity getPessoa() {
        return pessoa;
    }

    public void setPessoa(PessoaJpaEntity pessoa) {
        this.pessoa = pessoa;
    }

    public String getLogradouroTexto() {
        return logradouroTexto;
    }

    public void setLogradouroTexto(String logradouroTexto) {
        this.logradouroTexto = logradouroTexto;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairroTexto() {
        return bairroTexto;
    }

    public void setBairroTexto(String bairroTexto) {
        this.bairroTexto = bairroTexto;
    }

    public UUID getCidadeId() {
        return cidadeId;
    }

    public void setCidadeId(UUID cidadeId) {
        this.cidadeId = cidadeId;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }
}
