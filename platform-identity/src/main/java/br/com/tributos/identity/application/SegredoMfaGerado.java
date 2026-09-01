package br.com.tributos.identity.application;

import br.com.tributos.identity.domain.TipoMfa;

public record SegredoMfaGerado(TipoMfa tipo, String segredo, String uriProvisionamento, String mensagem) {

    public static SegredoMfaGerado totp(String segredo, String uriProvisionamento) {
        return new SegredoMfaGerado(TipoMfa.TOTP, segredo, uriProvisionamento, null);
    }

    public static SegredoMfaGerado email(String mensagem) {
        return new SegredoMfaGerado(TipoMfa.EMAIL, null, null, mensagem);
    }
}
