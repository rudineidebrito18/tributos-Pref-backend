package br.com.tributos.iptu.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CatalogoIptuRepository {

    List<CatalogoIptu> listar(TipoCatalogoIptu tipo);

    Optional<CatalogoIptu> buscarPorId(TipoCatalogoIptu tipo, UUID id);

    Optional<CatalogoIptu> buscarPorNome(TipoCatalogoIptu tipo, String nome);

    CatalogoIptu salvar(TipoCatalogoIptu tipo, CatalogoIptu item);

    void excluir(TipoCatalogoIptu tipo, UUID id);

    boolean existePorNome(TipoCatalogoIptu tipo, String nome, UUID ignorarId);
}
