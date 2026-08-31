package br.com.tributos.financeiro.adapters.out.pixbb;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "pix.bb.oauth")
public record BbOAuthProperties(
    @DefaultValue("https://oauth.bb.com.br") String producaoBaseUrl,
    @DefaultValue("https://oauth.hm.bb.com.br") String homologacaoBaseUrl
) {
}
