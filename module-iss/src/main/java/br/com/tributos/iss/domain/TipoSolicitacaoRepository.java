package br.com.tributos.iss.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TipoSolicitacaoRepository {

    List<TipoSolicitacao> listar();

    Optional<TipoSolicitacao> buscarPorId(UUID id);

    TipoSolicitacao salvar(TipoSolicitacao tipo);

    void excluir(UUID id);
}
