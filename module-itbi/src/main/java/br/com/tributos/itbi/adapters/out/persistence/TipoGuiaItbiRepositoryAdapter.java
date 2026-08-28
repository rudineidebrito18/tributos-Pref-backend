package br.com.tributos.itbi.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.itbi.domain.TipoGuiaItbi;
import br.com.tributos.itbi.domain.TipoGuiaItbiRepository;

@Component
public class TipoGuiaItbiRepositoryAdapter implements TipoGuiaItbiRepository {

    private final TipoGuiaItbiJpaRepository jpaRepository;

    public TipoGuiaItbiRepositoryAdapter(TipoGuiaItbiJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<TipoGuiaItbi> listarAtivos() {
        return jpaRepository.findByAtivoTrueOrderByNome().stream().map(this::paraDominio).toList();
    }

    @Override
    public Optional<TipoGuiaItbi> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(this::paraDominio);
    }

    private TipoGuiaItbi paraDominio(TipoGuiaItbiJpaEntity e) {
        return new TipoGuiaItbi(e.getId(), e.getTenantId(), e.getNome(), e.getAliquota(), e.isAtivo());
    }
}
