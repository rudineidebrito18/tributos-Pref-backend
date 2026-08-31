package br.com.tributos.financeiro.adapters.out.pixbb;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import br.com.tributos.financeiro.application.ConciliacaoPixProperties;

@Configuration
@EnableConfigurationProperties(ConciliacaoPixProperties.class)
public class PixFinanceiroConfiguration {
}
