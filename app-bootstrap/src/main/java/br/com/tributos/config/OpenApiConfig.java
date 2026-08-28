package br.com.tributos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Publica a documentação OpenAPI/Swagger UI da API — {@code springdoc-openapi} varre os
 * {@code @RestController} de todos os módulos de domínio presentes no classpath
 * automaticamente, sem anotação extra nos controllers. Documentação em
 * {@code /v3/api-docs} (JSON) e {@code /swagger-ui.html} (UI), liberadas em
 * {@code SecurityConfig.CAMINHOS_PUBLICOS} — descrevem a API, não expõem dado de tenant.
 */
@Configuration
public class OpenApiConfig {

    private static final String ESQUEMA_BEARER = "bearer-jwt";

    @Bean
    public OpenAPI tributosOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("Tributos API")
                .description(
                    "Backend multi-tenant do Sistema de Gestão Tributária Municipal. "
                        + "Endpoints sob /api/public/** são anônimos; os demais exigem "
                        + "Bearer JWT obtido em POST /api/auth/login (ver botão Authorize).")
                .version("v0.1 (Sprint 0)"))
            .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_BEARER))
            .components(new Components().addSecuritySchemes(
                ESQUEMA_BEARER,
                new SecurityScheme()
                    .name(ESQUEMA_BEARER)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
            ));
    }
}
