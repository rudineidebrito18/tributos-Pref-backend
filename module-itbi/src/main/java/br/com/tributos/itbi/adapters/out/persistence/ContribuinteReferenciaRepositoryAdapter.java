package br.com.tributos.itbi.adapters.out.persistence;

import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.itbi.domain.ContribuinteReferenciaRepository;

@Component
public class ContribuinteReferenciaRepositoryAdapter implements ContribuinteReferenciaRepository {

    private final ItbiContribuinteReferenciaJpaRepository jpaRepository;

    public ContribuinteReferenciaRepositoryAdapter(ItbiContribuinteReferenciaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existe(UUID contribuinteId) {
        return jpaRepository.existsById(contribuinteId);
    }
}
