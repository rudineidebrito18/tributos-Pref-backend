package br.com.tributos.identity.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SalvarConfiguracaoPixRequest(
    @NotNull Boolean ativo,
    @NotBlank String clientId,
    String clientSecret,
    @NotBlank String developerApplicationKey,
    @NotBlank @Size(max = 500) String escopos,
    @NotBlank @Size(max = 6) String numeroConvenio,
    @NotBlank @Size(max = 77) String chavePix,
    @NotBlank @Pattern(regexp = "[SN]") String indicadorCodigoBarras,
    String certificadoPath,
    String certificadoSenha,
    String webhookUrl,
    String webhookToken
) {
}
