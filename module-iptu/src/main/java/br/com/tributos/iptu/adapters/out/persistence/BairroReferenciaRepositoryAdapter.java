package br.com.tributos.iptu.adapters.out.persistence;

import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iptu.domain.BairroReferenciaRepository;

@Component("iptuBairroReferenciaRepositoryAdapter")
public class BairroReferenciaRepositoryAdapter implements BairroReferenciaRepository {

    private final IptuBairroReferenciaJpaRepository jpaRepository;

    public BairroReferenciaRepositoryAdapter(IptuBairroReferenciaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existe(UUID bairroId) {
        return jpaRepository.existsById(bairroId);
    }
}
