package br.com.tributos.identity.adapters.in.web.dto;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CriarTenantRequest(
    @NotBlank(message = "Informe o slug.")
    @Pattern(
        regexp = "^[a-z0-9][a-z0-9-]{1,48}[a-z0-9]$",
        message = "Slug deve ser minúsculo, alfanumérico, podendo usar hífen (ex.: sao-paulo)."
    )
    String slug,

    @NotBlank(message = "Informe o nome do tenant.")
    String nome,

    @NotBlank(message = "Informe a UF.")
    @Size(min = 2, max = 2, message = "UF deve ter 2 letras.")
    String uf,

    @NotBlank(message = "Informe o tipo de entidade (PREFEITURA ou CAMARA).")
    String tipoEntidade,

    String logoUrl,

    Cores cores,

    Set<String> modulosAtivos,

    /** Opcional — domínio próprio da prefeitura (ex. tributos.prefeitura.gov.br). */
    String dominioProprio,

    @NotBlank(message = "Informe o login do usuário administrador inicial.")
    String loginAdminInicial,

    @NotBlank(message = "Informe o e-mail do usuário administrador inicial.")
    @Email(message = "E-mail inválido.")
    String emailAdminInicial
) {

    /** Opcional — se omitido, o serviço aplica {@code PaletaTenant.padrao()}. */
    public record Cores(String accent, String accentDark, String accentSecondary, String accentTertiary) {
    }
}
