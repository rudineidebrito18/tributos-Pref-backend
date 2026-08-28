package br.com.tributos.iptu.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ZonaFiscalRepository {

    ZonaFiscal salvar(ZonaFiscal zonaFiscal);

    Optional<ZonaFiscal> buscarPorId(UUID id);

    List<ZonaFiscal> listar();

    List<ZonaFiscal> listarAtivas();

    boolean existe(UUID id);

    boolean existePorNome(String nome, UUID ignorarId);
}
