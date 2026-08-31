package br.com.tributos.iptu.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ImovelDestinacaoRepository {

    List<ImovelDestinacao> listar();

    Optional<ImovelDestinacao> buscarPorId(UUID id);

    ImovelDestinacao salvar(ImovelDestinacao destinacao);

    void excluir(UUID id);

    boolean existePorNome(String nome, UUID ignorarId);
}
