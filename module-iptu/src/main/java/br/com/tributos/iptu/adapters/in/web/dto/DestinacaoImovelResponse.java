package br.com.tributos.iptu.adapters.in.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.tributos.iptu.domain.ImovelDestinacao;

public record DestinacaoImovelResponse(
    UUID id,
    String nome,
    boolean ativo,
    UUID tipoImovelId,
    BigDecimal aliquotaIptu,
    Instant criadoEm
) {
    public static DestinacaoImovelResponse de(ImovelDestinacao destinacao) {
        return new DestinacaoImovelResponse(
            destinacao.id(),
            destinacao.nome(),
            destinacao.ativo(),
            destinacao.tipoImovelId(),
            destinacao.aliquotaIptu(),
            destinacao.criadoEm()
        );
    }
}
