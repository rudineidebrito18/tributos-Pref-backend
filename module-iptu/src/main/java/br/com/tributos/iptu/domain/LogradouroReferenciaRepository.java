package br.com.tributos.iptu.domain;

import java.util.UUID;

public interface LogradouroReferenciaRepository {

    boolean existe(UUID logradouroId);
}
