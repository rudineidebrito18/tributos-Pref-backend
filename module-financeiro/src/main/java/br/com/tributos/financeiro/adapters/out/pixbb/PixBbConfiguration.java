package br.com.tributos.financeiro.adapters.out.pixbb;

import java.time.Clock;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(BbOAuthProperties.class)
public class PixBbConfiguration {

    @Bean
    Clock pixBbClock() {
        return Clock.systemUTC();
    }
}
