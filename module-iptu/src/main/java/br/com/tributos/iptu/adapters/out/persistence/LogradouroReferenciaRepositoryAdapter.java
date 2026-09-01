package br.com.tributos.iptu.adapters.out.persistence;

import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.iptu.domain.LogradouroReferenciaRepository;

@Component("iptuLogradouroReferenciaRepositoryAdapter")
public class LogradouroReferenciaRepositoryAdapter implements LogradouroReferenciaRepository {

    private final IptuLogradouroReferenciaJpaRepository jpaRepository;

    public LogradouroReferenciaRepositoryAdapter(IptuLogradouroReferenciaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existe(UUID logradouroId) {
        return jpaRepository.existsById(logradouroId);
    }
}
