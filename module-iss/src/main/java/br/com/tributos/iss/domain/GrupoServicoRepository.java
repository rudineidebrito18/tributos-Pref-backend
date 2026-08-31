package br.com.tributos.iss.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GrupoServicoRepository {

    List<GrupoServico> listar();

    Optional<GrupoServico> buscarPorId(UUID id);

    GrupoServico salvar(GrupoServico grupo);

    void excluir(UUID id);

    boolean existePorCodigo(String codigo, UUID excetoId);
}
