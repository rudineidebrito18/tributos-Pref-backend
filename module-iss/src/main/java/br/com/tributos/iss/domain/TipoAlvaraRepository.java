package br.com.tributos.iss.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TipoAlvaraRepository {

    List<TipoAlvara> listar();

    Optional<TipoAlvara> buscarPorId(UUID id);

    TipoAlvara salvar(TipoAlvara tipoAlvara);

    boolean existePorNome(String nome, UUID excetoId);
}
