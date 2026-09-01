package br.com.tributos.identity.adapters.in.web.dto;

import br.com.tributos.identity.domain.TipoMfa;

public record HabilitarMfaRequest(TipoMfa tipo) {

    public TipoMfa tipoOuPadrao() {
        return tipo != null ? tipo : TipoMfa.TOTP;
    }
}
