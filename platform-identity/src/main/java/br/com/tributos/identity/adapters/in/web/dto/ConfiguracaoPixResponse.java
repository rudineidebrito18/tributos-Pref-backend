package br.com.tributos.identity.adapters.in.web.dto;

import java.time.Instant;
import java.util.UUID;

import br.com.tributos.identity.domain.ConfiguracaoPixBb;

public record ConfiguracaoPixResponse(
    UUID id,
    String ambiente,
    boolean ativo,
    String clientId,
    boolean clientSecretPreenchido,
    String developerApplicationKey,
    String escopos,
    String numeroConvenio,
    String chavePix,
    String indicadorCodigoBarras,
    String certificadoPath,
    boolean certificadoSenhaPreenchida,
    String webhookUrl,
    boolean webhookTokenPreenchido,
    Instant criadoEm,
    Instant atualizadoEm
) {
    public static ConfiguracaoPixResponse de(ConfiguracaoPixBb configuracao) {
        return new ConfiguracaoPixResponse(
            configuracao.getId(),
            configuracao.getAmbiente().name(),
            configuracao.isAtivo(),
            configuracao.getClientId(),
            configuracao.getClientSecret() != null && !configuracao.getClientSecret().isBlank(),
            configuracao.getDeveloperApplicationKey(),
            configuracao.getEscopos(),
            configuracao.getNumeroConvenio(),
            configuracao.getChavePix(),
            configuracao.getIndicadorCodigoBarras(),
            configuracao.getCertificadoPath(),
            configuracao.getCertificadoSenha() != null && !configuracao.getCertificadoSenha().isBlank(),
            configuracao.getWebhookUrl(),
            configuracao.getWebhookToken() != null && !configuracao.getWebhookToken().isBlank(),
            configuracao.getCriadoEm(),
            configuracao.getAtualizadoEm()
        );
    }
}
