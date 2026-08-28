package br.com.tributos.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.core.convert.converter.Converter;

/**
 * Resource Server de JWT auto-assinado + RBAC via claim {@code roles}. Substitui o
 * {@code permitAll()} do Sprint 0 inicial agora que o módulo de autenticação existe — ver
 * {@code br.com.tributos.identity.adapters.in.web.AuthController}.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] CAMINHOS_PUBLICOS = {
        "/api/public/**",
        "/api/auth/login",
        "/api/auth/refresh",
        "/api/auth/logout",
        "/api/auth/mfa/verificar",
        // path exato + /** para cobrir os grupos de probe liveness/readiness.
        "/actuator/health",
        "/actuator/health/**",
        "/actuator/info",
        // Documentação da API (springdoc) — descreve contratos, não expõe dado de tenant.
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
    };

    @Bean
    public SecurityFilterChain filterChain(
        HttpSecurity http,
        JwtDecoder jwtDecoder,
        TenantContextFilter tenantContextFilter,
        PublicApiRateLimitFilter publicApiRateLimitFilter
    ) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(CAMINHOS_PUBLICOS).permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                .decoder(jwtDecoder)
                .jwtAuthenticationConverter(jwtAuthenticationConverter())
            ))
            // TenantContextFilter precisa rodar DEPOIS que o Bearer token foi validado —
            // é dali que ele lê a claim `tenant_id` para preencher o TenantContext usado
            // pelo TenantAwareDataSource (RLS). Em endpoints públicos (sem JWT), o filtro
            // simplesmente não encontra Authentication e não faz nada.
            .addFilterBefore(publicApiRateLimitFilter, BearerTokenAuthenticationFilter.class)
            .addFilterAfter(tenantContextFilter, BearerTokenAuthenticationFilter.class)
            .build();
    }

    /** Mapeia a claim `roles` do JWT (lista de nomes de papel) para authorities `ROLE_*`, o formato que `@PreAuthorize("hasRole(...)")` espera. */
    private Converter<org.springframework.security.oauth2.jwt.Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("roles");
        authoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }

    /**
     * Mesma chave HMAC de {@code JwtGeradorToken} (módulo platform-identity) — as duas
     * classes leem a MESMA propriedade {@code app.security.jwt.secret} de propósito, mas
     * ficam desacopladas: esta classe só valida tokens (papel de Resource Server), a outra
     * só emite (papel de Authorization Server). Trocar de HMAC para chave assimétrica no
     * futuro só muda essas duas classes, nunca o restante da aplicação.
     */
    @Bean
    public JwtDecoder jwtDecoder(@Value("${app.security.jwt.secret}") String segredo) {
        SecretKeySpec chave = new SecretKeySpec(segredo.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(chave).macAlgorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
