package br.com.tributos.itbi.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record SalvarTipoCalculoGuiaItbiRequest(@NotBlank String descricao) {
}
