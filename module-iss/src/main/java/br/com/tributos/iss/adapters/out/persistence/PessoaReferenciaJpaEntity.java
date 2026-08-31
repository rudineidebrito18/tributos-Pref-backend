package br.com.tributos.iss.adapters.out.persistence;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Anti-corruption layer mínima para validar FK {@code pessoa_id} sem acoplar ao
 * bounded context de Cadastro.
 */
@Entity(name = "IssPessoaReferencia")
@Table(name = "pessoa")
public class PessoaReferenciaJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "cpf_cnpj", insertable = false, updatable = false)
    private String cpfCnpj;

    @Column(insertable = false, updatable = false)
    private String nome;

    @Column(insertable = false, updatable = false)
    private String email;

    protected PessoaReferenciaJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }
}
