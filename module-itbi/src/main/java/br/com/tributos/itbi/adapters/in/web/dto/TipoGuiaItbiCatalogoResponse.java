package br.com.tributos.itbi.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.tributos.itbi.domain.TipoGuiaItbi;

public record TipoGuiaItbiCatalogoResponse(
    UUID id,
    String nome,
    BigDecimal aliquota,
    boolean ativo,
    UUID tipoCalculoId,
    boolean permiteDesconto,
    boolean habilitaCalculoValor,
    BigDecimal valor,
    BigDecimal valorParcela,
    String secretaria,
    String cargo,
    UUID assinaturaDocumentoId
) {
    public static TipoGuiaItbiCatalogoResponse de(TipoGuiaItbi tipoGuia) {
        return new TipoGuiaItbiCatalogoResponse(
            tipoGuia.id(),
            tipoGuia.nome(),
            tipoGuia.aliquota(),
            tipoGuia.ativo(),
            tipoGuia.tipoCalculoId(),
            tipoGuia.permiteDesconto(),
            tipoGuia.habilitaCalculoValor(),
            tipoGuia.valor(),
            tipoGuia.valorParcela(),
            tipoGuia.secretaria(),
            tipoGuia.cargo(),
            tipoGuia.assinaturaDocumentoId()
        );
    }
}
