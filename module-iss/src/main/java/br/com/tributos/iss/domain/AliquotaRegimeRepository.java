package br.com.tributos.iss.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AliquotaRegimeRepository {

    List<AliquotaRegime> listarPorRegime(UUID regimeId);

    List<AliquotaRegime> listarVigentesPorRegime(UUID regimeId, java.time.LocalDate competencia);

    Optional<AliquotaRegime> buscarPorId(UUID id);

    AliquotaRegime salvar(AliquotaRegime faixa);

    void excluir(UUID id);
}
