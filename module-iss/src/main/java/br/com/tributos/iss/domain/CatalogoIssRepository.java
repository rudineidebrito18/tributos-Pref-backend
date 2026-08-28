package br.com.tributos.iss.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CatalogoIssRepository {

    List<CatalogoIss> listar(TipoCatalogoIss tipo);

    Optional<CatalogoIss> buscarPorId(TipoCatalogoIss tipo, UUID id);

    Optional<CatalogoIss> buscarPorNome(TipoCatalogoIss tipo, String nome);

    CatalogoIss salvar(TipoCatalogoIss tipo, CatalogoIss item);

    void excluir(TipoCatalogoIss tipo, UUID id);

    boolean existePorNome(TipoCatalogoIss tipo, String nome, UUID ignorarId);
}
