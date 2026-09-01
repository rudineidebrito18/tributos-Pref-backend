package br.com.tributos.iptu.domain;

import java.util.Optional;
import java.util.UUID;

public interface ContribuinteReferenciaRepository {

    boolean existe(UUID contribuinteId);

    Optional<UUID> buscarPessoaId(UUID contribuinteId);
}
