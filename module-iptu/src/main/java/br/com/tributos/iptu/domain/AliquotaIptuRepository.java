package br.com.tributos.iptu.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AliquotaIptuRepository {

    AliquotaIptu salvar(AliquotaIptu aliquota);

    Optional<AliquotaIptu> buscarPorChave(int exercicio, UUID destinacaoId, UUID zonaFiscalId);

    List<AliquotaIptu> listarPorExercicio(int exercicio);
}
