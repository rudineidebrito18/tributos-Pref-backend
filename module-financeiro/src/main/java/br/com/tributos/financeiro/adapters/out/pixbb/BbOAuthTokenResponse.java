package br.com.tributos.financeiro.adapters.out.pixbb;

record BbOAuthTokenResponse(
    String accessToken,
    int expiresIn,
    String scope
) {
}
