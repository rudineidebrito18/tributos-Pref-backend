package br.com.tributos.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PublicApiRateLimitProperties.class)
public class PublicApiConfig {
}
