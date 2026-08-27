package br.com.tributos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração mínima do Sprint 0. Só existe para liberar os caminhos públicos sem exigir
 * autenticação — o filtro JWT real (Resource Server + claims de tenant/roles, ver
 * PLANEJAMENTO_PROJETO.md §7.3 e §9) entra junto com o módulo de autenticação, ainda
 * pendente. CSRF desabilitado de propósito: a API é stateless (sem sessão de cookie), o
 * risco que CSRF mitiga não se aplica aqui — a proteção equivalente é o próprio requisito
 * de enviar um Bearer token válido em cada requisição autenticada.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] CAMINHOS_PUBLICOS = {
        "/api/public/**",
        "/actuator/health",
        "/actuator/info",
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(CAMINHOS_PUBLICOS).permitAll()
                // TODO (Sprint 0 — Autenticação): trocar por `.authenticated()` assim que o
                // filtro JWT existir. Liberado por ora para permitir smoke test manual dos
                // demais endpoints enquanto a autenticação não está pronta.
                .anyRequest().permitAll()
            )
            .build();
    }
}
