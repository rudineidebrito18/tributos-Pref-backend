package br.com.tributos.kernel.pixbb;

public record ResultadoTokenPixBb(
    String accessToken,
    int expiresIn,
    String scope
) {
}
