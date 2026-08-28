package br.com.tributos.iptu.adapters.out.persistence;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "IptuEnderecoReferencia")
@Table(name = "endereco")
public class EnderecoReferenciaJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    protected EnderecoReferenciaJpaEntity() {
    }
}
