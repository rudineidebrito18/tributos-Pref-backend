package br.com.tributos.iptu.adapters.in.web.dto;

import java.util.UUID;

import br.com.tributos.iptu.domain.HabiteseResponsavel;

public record HabiteseResponsavelResponse(
    UUID id,
    short ordem,
    String nome,
    String profissao,
    String documento
) {

    public static HabiteseResponsavelResponse de(HabiteseResponsavel responsavel) {
        return new HabiteseResponsavelResponse(
            responsavel.id(),
            responsavel.ordem(),
            responsavel.nome(),
            responsavel.profissao(),
            responsavel.documento()
        );
    }
}
