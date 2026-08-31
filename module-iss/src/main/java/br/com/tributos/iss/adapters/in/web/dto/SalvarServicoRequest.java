package br.com.tributos.iss.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SalvarServicoRequest(
    @NotBlank(message = "Informe o código LC 116 do serviço.")
    String codigoLc116,
    @NotBlank(message = "Informe a descrição do serviço.")
    String descricao,
    BigDecimal aliquotaMinima,
    BigDecimal aliquotaMaxima,
    boolean ativo,
    @NotNull(message = "Informe o grupo de serviço.")
    UUID grupoServicoId,
    String codigoNbs,
    String codigoTributacaoNacional,
    String indop,
    String cClassTrib
) {
}
