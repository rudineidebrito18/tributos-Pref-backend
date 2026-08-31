package br.com.tributos.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.public-api.rate-limit")
public record PublicApiRateLimitProperties(
    int maxRequests,
    long windowMs
) {
    public PublicApiRateLimitProperties {
        if (maxRequests <= 0) {
            maxRequests = 60;
        }
        if (windowMs <= 0) {
            windowMs = 60_000;
        }
    }
}
