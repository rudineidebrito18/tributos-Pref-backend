package br.com.tributos.iptu.domain;

import java.util.UUID;

public interface PessoaReferenciaRepository {

    boolean existe(UUID pessoaId);
}
