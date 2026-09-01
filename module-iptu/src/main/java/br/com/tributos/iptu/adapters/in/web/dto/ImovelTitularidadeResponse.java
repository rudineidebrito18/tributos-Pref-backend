package br.com.tributos.iptu.adapters.in.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.tributos.iptu.application.ImovelTitularidadeComContribuinte;
import br.com.tributos.iptu.domain.TipoRegistroTitularidade;

public record ImovelTitularidadeResponse(
    String contribuinte,
    TipoRegistroTitularidade tipoRegistro,
    BigDecimal porcentagem,
    Instant data
) {

    public static ImovelTitularidadeResponse de(ImovelTitularidadeComContribuinte item) {
        return new ImovelTitularidadeResponse(
            item.contribuinte(),
            item.historico().tipoRegistro(),
            item.historico().porcentagem(),
            item.historico().dataRegistro()
        );
    }
}
