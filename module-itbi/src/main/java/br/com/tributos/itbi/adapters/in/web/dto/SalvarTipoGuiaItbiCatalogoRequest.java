package br.com.tributos.itbi.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SalvarTipoGuiaItbiCatalogoRequest(
    @NotBlank String nome,
    @NotNull BigDecimal aliquota,
    boolean ativo,
    @NotNull UUID tipoCalculoId,
    boolean permiteDesconto,
    boolean habilitaCalculoValor,
    @NotNull BigDecimal valor,
    BigDecimal valorParcela,
    String secretaria,
    String cargo
) {
}
