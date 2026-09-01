package br.com.tributos.iptu.domain;

import java.util.List;
import java.util.UUID;

public interface ImovelTitularidadeHistoricoRepository {

    List<ImovelTitularidadeHistorico> listarPorImovel(UUID imovelId);

    ImovelTitularidadeHistorico salvar(ImovelTitularidadeHistorico historico);
}
