package br.com.tributos.identity.adapters.in.web.dto;

import br.com.tributos.kernel.pixbb.ResultadoTesteConexaoPixBb;

public record TestarConexaoPixResponse(
    boolean ok,
    Integer expiresIn,
    String scope,
    String erro
) {
    public static TestarConexaoPixResponse de(ResultadoTesteConexaoPixBb resultado) {
        return new TestarConexaoPixResponse(
            resultado.ok(),
            resultado.expiresIn(),
            resultado.scope(),
            resultado.erro()
        );
    }
}
