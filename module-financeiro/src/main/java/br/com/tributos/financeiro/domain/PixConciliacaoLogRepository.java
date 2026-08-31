package br.com.tributos.financeiro.domain;

import java.util.List;
import java.util.UUID;

public interface PixConciliacaoLogRepository {

    PixConciliacaoLog salvar(PixConciliacaoLog log);

    List<PixConciliacaoLog> listarPorGuiaId(UUID guiaId);
}
