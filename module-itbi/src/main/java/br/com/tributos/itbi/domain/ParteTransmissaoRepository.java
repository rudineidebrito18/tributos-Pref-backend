package br.com.tributos.itbi.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParteTransmissaoRepository {

    ParteTransmissao salvar(ParteTransmissao parte);

    Optional<ParteTransmissao> buscarPorId(UUID id);

    List<ParteTransmissao> listarPorGuiaEPapel(UUID guiaId, PapelParteTransmissao papel);

    boolean existePorGuiaContribuinteEPapel(UUID guiaId, UUID contribuinteId, PapelParteTransmissao papel, UUID idExcluir);

    void remover(UUID id);
}
