package br.com.tributos.identity.application.ports;

/**
 * Porta de saída para o segundo fator (TOTP — RFC 6238, compatível com Google
 * Authenticator, Authy, 1Password etc.). Implementação em {@code adapters.out.security}.
 */
public interface VerificadorMfa {

    /** Segredo aleatório codificado em Base32, no formato esperado pelos apps de TOTP. */
    String gerarSegredo();

    /** URI {@code otpauth://} para gerar o QR code de provisionamento no app do usuário. */
    String gerarUriProvisionamento(String segredo, String identificadorUsuario);

    boolean validarCodigo(String segredo, String codigo);
}
