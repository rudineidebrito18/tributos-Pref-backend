package br.com.tributos.financeiro.domain;

import java.util.Optional;
import java.util.UUID;

public interface FormaPagamentoRepository {

    Optional<FormaPagamento> buscarPorCodigo(String codigo);

    Optional<FormaPagamento> buscarPorId(UUID id);
}
