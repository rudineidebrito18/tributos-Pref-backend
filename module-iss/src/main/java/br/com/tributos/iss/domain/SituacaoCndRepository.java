package br.com.tributos.iss.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SituacaoCndRepository {

    List<SituacaoCnd> listar();

    Optional<SituacaoCnd> buscarPorId(UUID id);

    SituacaoCnd salvar(SituacaoCnd situacao);

    void excluir(UUID id);

    boolean existePorDescricao(String descricao, UUID excetoId);
}
