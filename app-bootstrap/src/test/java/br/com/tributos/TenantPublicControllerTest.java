package br.com.tributos;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Sobe um Postgres real (Testcontainers), roda as migrations Flyway de verdade e chama o
 * endpoint público de branding ponta a ponta — inclusive validando o seed do tenant
 * "demo" (V2__seed_tenant_demo.sql), o mesmo slug que o frontend usa como fallback local.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Testcontainers
class TenantPublicControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveRetornarBrandingDoTenantDemoSemeadoPelaMigration() throws Exception {
        mockMvc.perform(get("/api/public/tenants/demo/branding"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.slug").value("demo"))
            .andExpect(jsonPath("$.tipoEntidade").value("prefeitura"))
            .andExpect(jsonPath("$.cores.accent").value("#4c8dff"))
            .andExpect(jsonPath("$.modulosAtivos", Matchers.hasSize(5)));
    }

    @Test
    void deveRetornar404ParaTenantInexistenteOuInativo() throws Exception {
        mockMvc.perform(get("/api/public/tenants/nao-existe/branding"))
            .andExpect(status().isNotFound());
    }
}
