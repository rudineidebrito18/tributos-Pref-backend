package br.com.tributos.iptu.adapters.out.persistence;

import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iptu.domain.EnderecoReferenciaRepository;

@Component("iptuEnderecoReferenciaRepositoryAdapter")
public class EnderecoReferenciaRepositoryAdapter implements EnderecoReferenciaRepository {

    private final IptuEnderecoReferenciaJpaRepository jpaRepository;

    public EnderecoReferenciaRepositoryAdapter(IptuEnderecoReferenciaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existe(UUID enderecoId) {
        return jpaRepository.existsById(enderecoId);
    }
}
