package br.com.tributos.itbi.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TipoCalculoGuiaItbiRepository {

    List<TipoCalculoGuiaItbi> listar();

    Optional<TipoCalculoGuiaItbi> buscarPorId(UUID id);

    TipoCalculoGuiaItbi salvar(TipoCalculoGuiaItbi tipoCalculo);

    void excluir(UUID id);

    boolean existePorDescricao(String descricao, UUID ignorarId);
}
