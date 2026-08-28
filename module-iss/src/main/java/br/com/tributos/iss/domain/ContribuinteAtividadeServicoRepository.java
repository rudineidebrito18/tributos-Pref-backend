package br.com.tributos.iss.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContribuinteAtividadeServicoRepository {

    List<ContribuinteAtividadeServico> listarPorContribuinte(UUID contribuinteId);

    Optional<ContribuinteAtividadeServico> buscarPorId(UUID id);

    ContribuinteAtividadeServico salvar(ContribuinteAtividadeServico vinculo);

    void excluir(UUID id);

    boolean existeVinculo(UUID contribuinteId, UUID atividadeId, UUID servicoId);
}
