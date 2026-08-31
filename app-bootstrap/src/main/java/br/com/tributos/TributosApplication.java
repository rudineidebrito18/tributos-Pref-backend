package br.com.tributos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Ponto de entrada. Escaneia {@code br.com.tributos} inteiro — cada módulo de domínio
 * (platform-identity, e os futuros module-iss/module-iptu/...) só precisa estar no
 * classpath (declarado como dependência aqui) para seus {@code @Component}/
 * {@code @RestController}/{@code @Entity} serem descobertos, sem configuração adicional.
 */
@SpringBootApplication
@EnableScheduling
public class TributosApplication {

    public static void main(String[] args) {
        SpringApplication.run(TributosApplication.class, args);
    }
}
