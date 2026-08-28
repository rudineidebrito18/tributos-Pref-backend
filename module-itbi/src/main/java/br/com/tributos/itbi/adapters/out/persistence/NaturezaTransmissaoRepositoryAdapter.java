package br.com.tributos.itbi.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.itbi.domain.NaturezaTransmissao;
import br.com.tributos.itbi.domain.NaturezaTransmissaoRepository;

@Component
public class NaturezaTransmissaoRepositoryAdapter implements NaturezaTransmissaoRepository {

    private final NaturezaTransmissaoJpaRepository jpaRepository;

    public NaturezaTransmissaoRepositoryAdapter(NaturezaTransmissaoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<NaturezaTransmissao> listarAtivas() {
        return jpaRepository.findByAtivoTrueOrderByNome().stream().map(this::paraDominio).toList();
    }

    @Override
    public Optional<NaturezaTransmissao> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(this::paraDominio);
    }

    private NaturezaTransmissao paraDominio(NaturezaTransmissaoJpaEntity e) {
        return new NaturezaTransmissao(e.getId(), e.getTenantId(), e.getNome(), e.isAtivo());
    }
}
