package br.com.tributos.iptu.application;

import br.com.tributos.iptu.domain.ImovelObservacao;

public record ImovelObservacaoComUsuario(
    ImovelObservacao observacao,
    String usuario
) {
}
