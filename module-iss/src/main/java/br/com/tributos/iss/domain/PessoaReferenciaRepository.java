package br.com.tributos.iss.domain;

import java.util.UUID;

public interface PessoaReferenciaRepository {

    boolean existe(UUID pessoaId);

    java.util.Optional<PessoaDadosResumo> buscarDados(java.util.UUID pessoaId);
}
