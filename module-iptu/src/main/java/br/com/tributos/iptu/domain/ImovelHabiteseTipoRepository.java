package br.com.tributos.iptu.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ImovelHabiteseTipoRepository {

    List<ImovelHabiteseTipo> listar();

    Optional<ImovelHabiteseTipo> buscarPorId(UUID id);

    ImovelHabiteseTipo salvar(ImovelHabiteseTipo tipo);

    void excluir(UUID id);

    boolean existePorNome(String nome, UUID ignorarId);
}
