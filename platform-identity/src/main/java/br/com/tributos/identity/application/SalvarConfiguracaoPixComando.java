package br.com.tributos.identity.application;

import br.com.tributos.identity.domain.AmbientePixBb;

public record SalvarConfiguracaoPixComando(
    AmbientePixBb ambiente,
    boolean ativo,
    String clientId,
    String clientSecret,
    String developerApplicationKey,
    String escopos,
    String numeroConvenio,
    String chavePix,
    String indicadorCodigoBarras,
    String certificadoPath,
    String certificadoSenha,
    String webhookUrl,
    String webhookToken
) {
}
