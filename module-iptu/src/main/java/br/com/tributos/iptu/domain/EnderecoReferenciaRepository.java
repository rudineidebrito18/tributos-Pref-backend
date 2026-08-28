package br.com.tributos.iptu.domain;

import java.util.UUID;

public interface EnderecoReferenciaRepository {

    boolean existe(UUID enderecoId);
}
