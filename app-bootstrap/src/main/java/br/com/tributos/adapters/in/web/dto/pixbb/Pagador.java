package br.com.tributos.adapters.in.web.dto.pixbb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Pagador(String cpf, String cnpj, String nome) {
}
