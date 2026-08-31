package br.com.tributos.iss.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SolicitacaoRepository {

    Solicitacao salvar(Solicitacao solicitacao);

    Optional<Solicitacao> buscarPorId(UUID id);

    Page<Solicitacao> listar(UUID tipoSolicitacaoId, UUID statusSolicitacaoId, Pageable pageable);
}
