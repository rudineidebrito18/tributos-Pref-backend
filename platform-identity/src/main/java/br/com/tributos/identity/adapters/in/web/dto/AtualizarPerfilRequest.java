package br.com.tributos.identity.adapters.in.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AtualizarPerfilRequest(
    String nome,
    String login,
    String email,
    String password1,
    String password2
) {
}
