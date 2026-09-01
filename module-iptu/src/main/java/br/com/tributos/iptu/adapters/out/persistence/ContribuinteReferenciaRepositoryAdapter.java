package br.com.tributos.iptu.adapters.out.persistence;

import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iptu.domain.ContribuinteReferenciaRepository;

@Component("iptuContribuinteReferenciaRepositoryAdapter")
public class ContribuinteReferenciaRepositoryAdapter implements ContribuinteReferenciaRepository {

    private final IptuContribuinteReferenciaJpaRepository jpaRepository;

    public ContribuinteReferenciaRepositoryAdapter(IptuContribuinteReferenciaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existe(UUID contribuinteId) {
        return jpaRepository.existsById(contribuinteId);
    }

    @Override
    public java.util.Optional<UUID> buscarPessoaId(UUID contribuinteId) {
        return jpaRepository.findById(contribuinteId).map(ContribuinteReferenciaJpaEntity::getPessoaId);
    }
}
