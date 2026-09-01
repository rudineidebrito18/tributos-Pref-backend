package br.com.tributos.iptu.adapters.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    SituacaoImovel situacao,
    Short anoExercicio,
    LocalDate dataInclusao,
    BigDecimal areaTotal,
    BigDecimal frente,
    BigDecimal fundos,
    BigDecimal ladoEsquerdo,
    BigDecimal ladoDireito,
    String quadra,
    String lote,
    String loteamento,
    String edificio,
    String bloco,
    String sala,
    String apartamento,
    UUID bairroIptuId,
    UUID logradouroIptuId,
    BigDecimal valorVenalUnidade,
    BigDecimal valorAvaliacao,
    UUID enderecoCorrespondenciaId,
    String observacao
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
            imovel.situacao(),
            imovel.anoExercicio(),
            imovel.dataInclusao(),
            imovel.areaTotal(),
            imovel.frente(),
            imovel.fundos(),
            imovel.ladoEsquerdo(),
            imovel.ladoDireito(),
            imovel.quadra(),
            imovel.lote(),
            imovel.loteamento(),
            imovel.edificio(),
            imovel.bloco(),
            imovel.sala(),
            imovel.apartamento(),
            imovel.bairroIptuId(),
            imovel.logradouroIptuId(),
            imovel.valorVenalUnidade(),
            imovel.valorAvaliacao(),
            imovel.enderecoCorrespondenciaId(),
            imovel.observacao()
        );
    }
}
