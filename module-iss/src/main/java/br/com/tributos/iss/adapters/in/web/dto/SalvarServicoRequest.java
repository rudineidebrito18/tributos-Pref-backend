package br.com.tributos.iss.adapters.in.web.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;

public record SalvarServicoRequest(
    @NotBlank(message = "Informe o código LC 116 do serviço.")
    String codigoLc116,
    @NotBlank(message = "Informe a descrição do serviço.")
    String descricao,
    BigDecimal aliquotaMinima,
    BigDecimal aliquotaMaxima,
    boolean ativo
) {
}
