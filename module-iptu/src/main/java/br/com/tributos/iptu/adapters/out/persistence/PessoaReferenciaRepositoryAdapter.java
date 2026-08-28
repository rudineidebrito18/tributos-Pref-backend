package br.com.tributos.iptu.adapters.out.persistence;

import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iptu.domain.PessoaReferenciaRepository;

@Component
public class PessoaReferenciaRepositoryAdapter implements PessoaReferenciaRepository {

    private final PessoaReferenciaJpaRepository jpaRepository;

    public PessoaReferenciaRepositoryAdapter(PessoaReferenciaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existe(UUID pessoaId) {
        return jpaRepository.existsById(pessoaId);
    }
}
