package br.com.tributos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Onboarding administrativo de tenants — ponta a ponta com Postgres real (Testcontainers).
 * Cobre autorização ({@code PLATAFORMA_ADMIN} vs {@code ADMIN_TENANT}), criação do tenant
 * com usuário inicial e isolamento básico (admin de um tenant não pode criar outro).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Testcontainers
class TenantAdminControllerTest {

    private static final String TENANT_DEMO = "demo";
    private static final String TENANT_PLATAFORMA = "_plataforma";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveNegarCriacaoDeTenantParaAdminDePrefeitura() throws Exception {
        String token = login(TENANT_DEMO, "admin", "Demo@123");

        mockMvc.perform(post("/api/admin/tenants")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpoCriarTenant("cidade-teste-a", "admin-a@teste.local")))
            .andExpect(status().isForbidden());
    }

    @Test
    void deveCriarTenantComAdminInicialEAutenticarNoNovoSlug() throws Exception {
        String tokenPlataforma = login(TENANT_PLATAFORMA, "plataforma-admin", "Demo@123");
        String slug = "cidade-piloto-" + System.nanoTime();

        String resposta = mockMvc.perform(post("/api/admin/tenants")
                .header("Authorization", "Bearer " + tokenPlataforma)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpoCriarTenant(slug, "admin@" + slug + ".local")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.slug").value(slug))
            .andExpect(jsonPath("$.senhaTemporaria").isNotEmpty())
            .andReturn().getResponse().getContentAsString();

        JsonNode criado = objectMapper.readTree(resposta);
        String senhaTemporaria = criado.get("senhaTemporaria").asText();
        String loginAdmin = criado.get("usuarioAdminLogin").asText();

        JsonNode loginNovoTenant = loginRetornandoJson(slug, loginAdmin, senhaTemporaria);
        assertThat(loginNovoTenant.get("mfaNecessario").asBoolean()).isFalse();
        assertThat(loginNovoTenant.get("tokens").get("accessToken").asText()).isNotBlank();
    }

    @Test
    void deveRejeitarSlugDuplicado() throws Exception {
        String tokenPlataforma = login(TENANT_PLATAFORMA, "plataforma-admin", "Demo@123");

        mockMvc.perform(post("/api/admin/tenants")
                .header("Authorization", "Bearer " + tokenPlataforma)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpoCriarTenant("demo", "outro-admin@demo.local")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.mensagem").value("Já existe um tenant com o slug \"demo\"."));
    }

    private String login(String tenantSlug, String login, String senha) throws Exception {
        JsonNode resposta = loginRetornandoJson(tenantSlug, login, senha);
        return resposta.get("tokens").get("accessToken").asText();
    }

    private JsonNode loginRetornandoJson(String tenantSlug, String login, String senha) throws Exception {
        String corpo = mockMvc.perform(post("/api/auth/login")
                .header("X-Tenant-Slug", tenantSlug)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"" + login + "\",\"senha\":\"" + senha + "\"}"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo);
    }

    private static String corpoCriarTenant(String slug, String emailAdmin) {
        return """
            {
              "slug": "%s",
              "nome": "Prefeitura de Teste",
              "uf": "SP",
              "tipoEntidade": "PREFEITURA",
              "modulosAtivos": ["cadastro", "iss"],
              "loginAdminInicial": "admin",
              "emailAdminInicial": "%s"
            }
            """.formatted(slug, emailAdmin);
    }
}
