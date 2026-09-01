package br.com.tributos.iptu.application;

import br.com.tributos.iptu.domain.ImovelTitularidadeHistorico;

public record ImovelTitularidadeComContribuinte(
    ImovelTitularidadeHistorico historico,
    String contribuinte
) {
}
