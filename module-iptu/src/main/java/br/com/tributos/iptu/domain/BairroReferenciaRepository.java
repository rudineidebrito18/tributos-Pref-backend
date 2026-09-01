package br.com.tributos.iptu.domain;

import java.util.UUID;

public interface BairroReferenciaRepository {

    boolean existe(UUID bairroId);
}
