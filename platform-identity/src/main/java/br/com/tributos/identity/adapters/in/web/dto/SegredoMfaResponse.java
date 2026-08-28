package br.com.tributos.identity.adapters.in.web.dto;

import br.com.tributos.identity.application.SegredoMfaGerado;

public record SegredoMfaResponse(String segredo, String uriProvisionamento) {

    public static SegredoMfaResponse de(SegredoMfaGerado gerado) {
        return new SegredoMfaResponse(gerado.segredo(), gerado.uriProvisionamento());
    }
}
