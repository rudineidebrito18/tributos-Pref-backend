package br.com.tributos.cadastro.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LogradouroRepository {

    List<Logradouro> listar(UUID cidadeId, UUID bairroId);

    Optional<Logradouro> buscarPorId(UUID id);

    Logradouro salvar(Logradouro logradouro);

    void excluir(UUID id);
}
