package br.com.tributos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.tributos.shared.cripto.CifradorAesGcm;
import br.com.tributos.shared.cripto.CifradorSegredo;

@Configuration
public class CriptografiaConfig {

    @Bean
    CifradorSegredo cifradorSegredo(@Value("${app.secret-key}") String chave) {
        return new CifradorAesGcm(chave);
    }
}
