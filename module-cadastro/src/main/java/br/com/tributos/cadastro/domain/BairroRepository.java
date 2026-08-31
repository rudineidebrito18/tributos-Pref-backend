package br.com.tributos.cadastro.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BairroRepository {

    List<Bairro> listar(UUID cidadeId);

    Optional<Bairro> buscarPorId(UUID id);

    Bairro salvar(Bairro bairro);

    void excluir(UUID id);

    boolean existePorNome(UUID cidadeId, String nome, UUID ignorarId);
}
