package br.com.tributos.iptu.domain;

import java.util.List;
import java.util.UUID;

public interface LancamentoParcelaRepository {

    List<LancamentoParcela> salvarTodos(List<LancamentoParcela> parcelas);

    List<LancamentoParcela> listarPorLancamento(UUID lancamentoId);
}
