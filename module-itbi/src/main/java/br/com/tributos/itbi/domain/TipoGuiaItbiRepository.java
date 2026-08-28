package br.com.tributos.itbi.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TipoGuiaItbiRepository {

    List<TipoGuiaItbi> listarAtivos();

    Optional<TipoGuiaItbi> buscarPorId(UUID id);
}
