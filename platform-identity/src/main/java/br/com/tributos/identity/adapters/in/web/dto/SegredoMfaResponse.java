package br.com.tributos.identity.adapters.in.web.dto;

import br.com.tributos.identity.application.SegredoMfaGerado;
import br.com.tributos.identity.domain.TipoMfa;

public record SegredoMfaResponse(
    TipoMfa tipo,
    String segredo,
    String uriProvisionamento,
    String mensagem
) {

    public static SegredoMfaResponse de(SegredoMfaGerado gerado) {
        return new SegredoMfaResponse(gerado.tipo(), gerado.segredo(), gerado.uriProvisionamento(), gerado.mensagem());
    }
}
