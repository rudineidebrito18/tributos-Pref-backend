package br.com.tributos.kernel.pixbb;

public record ResultadoTesteConexaoPixBb(
    boolean ok,
    Integer expiresIn,
    String scope,
    String erro
) {
}
