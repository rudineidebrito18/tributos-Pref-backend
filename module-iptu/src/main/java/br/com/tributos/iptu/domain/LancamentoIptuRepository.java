package br.com.tributos.iptu.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LancamentoIptuRepository {

    LancamentoIptu salvar(LancamentoIptu lancamento);

    Optional<LancamentoIptu> buscarPorId(UUID id);

    Optional<LancamentoIptu> buscarPorImovelEExercicio(UUID imovelId, int exercicio);

    Page<LancamentoIptu> listar(Integer exercicio, UUID imovelId, Pageable pageable);
}
