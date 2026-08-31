package br.com.tributos.cadastro.adapters.in.web.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SalvarLogradouroRequest(
    @NotNull UUID cidadeId,
    UUID bairroId,
    @Size(max = 50) String tipo,
    @NotBlank @Size(max = 200) String nome,
    @Size(max = 10) String cep
) {
}
