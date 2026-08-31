package br.com.tributos.kernel.pixbb;

import java.util.UUID;

/**
 * Credenciais desacopladas de {@code platform-identity} para uso pelos adapters PIX BB
 * em {@code module-financeiro}.
 */
public record CredenciaisPixBb(
    UUID tenantId,
    String ambiente,
    String clientId,
    String clientSecret,
    String escopos,
    String certificadoPath,
    String certificadoSenha
) {
}
