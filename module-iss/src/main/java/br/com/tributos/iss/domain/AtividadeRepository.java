package br.com.tributos.iss.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AtividadeRepository {

    List<Atividade> listar();

    Optional<Atividade> buscarPorId(UUID id);

    Atividade salvar(Atividade atividade);

    void excluir(UUID id);

    boolean existePorCodigo(String codigo, UUID ignorarId);
}
