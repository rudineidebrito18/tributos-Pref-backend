package br.com.tributos.iptu.domain;

import java.util.List;
import java.util.UUID;

public interface ImovelObservacaoRepository {

    ImovelObservacao salvar(ImovelObservacao observacao);

    List<ImovelObservacao> listarPorImovel(UUID imovelId);
}
