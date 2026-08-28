package br.com.tributos.identity.application;

/** Segredo TOTP recém-gerado, ainda não confirmado. O segredo em si só é exposto aqui — pensado para o front renderizar como texto alternativo ao QR code. */
public record SegredoMfaGerado(String segredo, String uriProvisionamento) {
}
