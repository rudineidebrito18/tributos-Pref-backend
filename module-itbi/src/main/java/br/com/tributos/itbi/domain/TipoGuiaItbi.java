package br.com.tributos.itbi.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record TipoGuiaItbi(
    UUID id,
    UUID tenantId,
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
}
