package br.com.tributos.cadastro.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TerritorioRepository {

    List<Estado> listarEstados();

    List<Cidade> listarCidadesPorUf(String uf);

    Optional<Cidade> buscarCidadePorId(UUID id);
}
