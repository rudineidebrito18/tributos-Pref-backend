package br.com.tributos.iss.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SolicitacaoCredenciamentoRepository {

    SolicitacaoCredenciamento salvar(SolicitacaoCredenciamento solicitacao);

    Optional<SolicitacaoCredenciamento> buscarPorId(UUID id);

    Page<SolicitacaoCredenciamento> listar(Pageable pageable);

    Optional<SolicitacaoCredenciamento> buscarEmAnalisePorContribuinte(UUID contribuinteId);
}
