package br.com.tributos.iss.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LocalIncidenciaRepository {

    List<LocalIncidencia> listar();

    Optional<LocalIncidencia> buscarPorId(UUID id);

    LocalIncidencia salvar(LocalIncidencia local);

    void excluir(UUID id);

    boolean existePorDescricao(String descricao, UUID excetoId);
}
