package br.com.tributos.iptu.adapters.out.persistence;

import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iptu.domain.EnderecoReferenciaRepository;

@Component
public class EnderecoReferenciaRepositoryAdapter implements EnderecoReferenciaRepository {

    private final EnderecoReferenciaJpaRepository jpaRepository;

    public EnderecoReferenciaRepositoryAdapter(EnderecoReferenciaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existe(UUID enderecoId) {
        return jpaRepository.existsById(enderecoId);
    }
}
