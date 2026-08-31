package br.com.tributos.iss.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StatusSolicitacaoRepository {

    List<StatusSolicitacao> listar();

    Optional<StatusSolicitacao> buscarPorId(UUID id);

    StatusSolicitacao salvar(StatusSolicitacao status);

    void excluir(UUID id);
}
