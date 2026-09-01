package br.com.tributos.iptu.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ImovelProprietarioRepository {

    ImovelProprietario salvar(ImovelProprietario proprietario);

    List<ImovelProprietario> listarPorImovel(UUID imovelId);

    Optional<ImovelProprietario> buscarPorId(UUID id);

    void remover(UUID id);

    void removerPorImovel(UUID imovelId);

    boolean existePorImovelEContribuinte(UUID imovelId, UUID contribuinteId, UUID idExcluir);

    Optional<ImovelProprietario> buscarPrincipalPorImovel(UUID imovelId);
}
