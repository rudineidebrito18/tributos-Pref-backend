package br.com.tributos.cadastro.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.cadastro.domain.Cidade;
import br.com.tributos.cadastro.domain.Estado;
import br.com.tributos.cadastro.domain.TerritorioRepository;

@Component
public class TerritorioRepositoryAdapter implements TerritorioRepository {

    private final EstadoJpaRepository estadoJpaRepository;
    private final CidadeJpaRepository cidadeJpaRepository;

    public TerritorioRepositoryAdapter(EstadoJpaRepository estadoJpaRepository, CidadeJpaRepository cidadeJpaRepository) {
        this.estadoJpaRepository = estadoJpaRepository;
        this.cidadeJpaRepository = cidadeJpaRepository;
    }

    @Override
    public List<Estado> listarEstados() {
        return estadoJpaRepository.findAllByOrderByNomeAsc().stream()
            .map(e -> new Estado(e.getId(), e.getSigla(), e.getNome()))
            .toList();
    }

    @Override
    public List<Cidade> listarCidadesPorUf(String uf) {
        return cidadeJpaRepository.findByEstadoSigla(uf.toUpperCase()).stream()
            .map(c -> new Cidade(c.getId(), c.getNome(), c.getEstado().getSigla(), c.getCodigoIbge()))
            .toList();
    }

    @Override
    public Optional<Cidade> buscarCidadePorId(UUID id) {
        return cidadeJpaRepository.findById(id)
            .map(c -> new Cidade(c.getId(), c.getNome(), c.getEstado().getSigla(), c.getCodigoIbge()));
    }
}
