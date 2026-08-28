package br.com.tributos.iptu.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.tributos.iptu.domain.Imovel;
import br.com.tributos.iptu.domain.SituacaoImovel;

public record ImovelResponse(
    UUID id,
    long numeroCadastro,
    String codigoLegado,
    UUID proprietarioId,
    UUID tipoId,
    UUID enderecoId,
    BigDecimal areaTerreno,
    BigDecimal areaConstruida,
    UUID destinacaoId,
    UUID tipoEdificacaoId,
    UUID tipoLimitacaoId,
    UUID zonaFiscalId,
    BigDecimal valorVenalTerreno,
    BigDecimal valorVenalConstrucao,
    SituacaoImovel situacao
) {

    public static ImovelResponse de(Imovel imovel) {
        return new ImovelResponse(
            imovel.id(),
            imovel.numeroCadastro(),
            imovel.codigoLegado(),
            imovel.proprietarioId(),
            imovel.tipoId(),
            imovel.enderecoId(),
            imovel.areaTerreno(),
            imovel.areaConstruida(),
            imovel.destinacaoId(),
            imovel.tipoEdificacaoId(),
            imovel.tipoLimitacaoId(),
            imovel.zonaFiscalId(),
            imovel.valorVenalTerreno(),
            imovel.valorVenalConstrucao(),
            imovel.situacao()
        );
    }
}
