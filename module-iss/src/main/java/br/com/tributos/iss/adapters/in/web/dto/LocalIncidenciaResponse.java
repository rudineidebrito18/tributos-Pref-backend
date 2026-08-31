package br.com.tributos.iss.adapters.in.web.dto;

import java.util.UUID;

import br.com.tributos.iss.domain.LocalIncidencia;

public record LocalIncidenciaResponse(UUID id, String descricao, boolean ativo) {

    public static LocalIncidenciaResponse de(LocalIncidencia local) {
        return new LocalIncidenciaResponse(local.id(), local.descricao(), local.ativo());
    }
}
