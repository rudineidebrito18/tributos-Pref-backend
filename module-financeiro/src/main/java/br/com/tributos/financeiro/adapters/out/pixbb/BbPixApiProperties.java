package br.com.tributos.financeiro.adapters.out.pixbb;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "pix.bb.api")
public record BbPixApiProperties(
    @DefaultValue("https://pix-bb.mtls.api.bb.com.br/v1") String producaoBaseUrl,
    @DefaultValue("https://pix-bb.mtls.api.hm.bb.com.br/v1") String homologacaoBaseUrl
) {
}
