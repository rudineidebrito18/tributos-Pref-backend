package br.com.tributos.iss.adapters.in.web.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public record SalvarTipoSolicitacaoRequest(
    @NotBlank(message = "Informe a descrição.")
    String descricao,
    UUID usuarioNotificarId,
    boolean ativo
) {
}
