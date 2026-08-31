package br.com.tributos.iptu.adapters.in.web.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SalvarHabiteseTipoRequest(
    @NotBlank String nome,
    boolean ativo,
    @NotBlank String titulo,
    boolean permiteDesconto,
    boolean habilitaCalculoValor,
    @NotNull BigDecimal valor,
    String secretaria,
    String cargo
) {
}
