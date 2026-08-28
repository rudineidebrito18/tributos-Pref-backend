package br.com.tributos.iptu.adapters.in.web.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record ImportarImovelLegadoRequest(
    @NotEmpty(message = "Informe ao menos um imóvel para importar.")
    List<@Valid SalvarImovelRequest> itens
) {
}
