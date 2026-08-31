package br.com.tributos.iss.adapters.out.persistence;

import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iss.domain.PessoaReferenciaRepository;

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

    @Override
    public java.util.Optional<br.com.tributos.iss.domain.PessoaDadosResumo> buscarDados(UUID pessoaId) {
        return jpaRepository.findById(pessoaId)
            .map(p -> new br.com.tributos.iss.domain.PessoaDadosResumo(
                p.getCpfCnpj(), p.getNome(), p.getEmail()));
    }
}
