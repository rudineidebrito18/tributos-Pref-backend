package br.com.tributos.iss.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServicoRepository {

    List<Servico> listar();

    Optional<Servico> buscarPorId(UUID id);

    Servico salvar(Servico servico);

    void excluir(UUID id);

    boolean existePorCodigoLc116(String codigoLc116, UUID ignorarId);
}
