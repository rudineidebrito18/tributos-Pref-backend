package br.com.tributos.iptu.adapters.in.web.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import br.com.tributos.iptu.domain.CertidaoNegativaImovel;

public record CertidaoNegativaImovelResponse(
    UUID id,
    UUID imovelId,
    long numero,
    LocalDate dataEmissao,
    LocalDate validade,
    String codigoVerificacao,
    Instant dataEmissaoTs
) {

    public static CertidaoNegativaImovelResponse de(CertidaoNegativaImovel certidao) {
        return new CertidaoNegativaImovelResponse(
            certidao.id(),
            certidao.imovelId(),
            certidao.numero(),
            certidao.dataEmissao(),
            certidao.validade(),
            certidao.codigoVerificacao(),
            certidao.dataEmissaoTs()
        );
    }
}
