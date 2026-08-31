package br.com.tributos.kernel.pixbb;

import java.util.UUID;

/** Configuração PIX BB ativa de um tenant — credenciais decifradas para uso operacional. */
public record ConfiguracaoPixOperacional(
    UUID tenantId,
    String ambiente,
    String clientId,
    String clientSecret,
    String developerApplicationKey,
    String escopos,
    String numeroConvenio,
    String chavePix,
    String indicadorCodigoBarras,
    String certificadoPath,
    String certificadoSenha,
    String webhookToken
) {
    public CredenciaisPixBb credenciais() {
        return new CredenciaisPixBb(
            tenantId,
            ambiente,
            clientId,
            clientSecret,
            escopos,
            certificadoPath,
            certificadoSenha
        );
    }
}
