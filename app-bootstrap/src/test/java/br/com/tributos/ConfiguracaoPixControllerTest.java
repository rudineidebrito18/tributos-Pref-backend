package br.com.tributos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Testcontainers
class ConfiguracaoPixControllerTest {

    private static final String TENANT_DEMO = "demo";
    private static final String TENANT_PLATAFORMA = "_plataforma";
    private static final String SEGREDO = "segredo123";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void adminDeveSalvarSemExporSegredoEFiscalDeveReceber403() throws Exception {
        String tokenAdmin = login(TENANT_DEMO, "admin", "Demo@123");

        mockMvc.perform(put("/api/plataforma/configuracao-pix/SANDBOX")
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpoConfig(true)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.clientSecretPreenchido").value(true))
            .andExpect(jsonPath("$.clientId").value("client-demo"));

        String respostaGet = mockMvc.perform(get("/api/plataforma/configuracao-pix")
                .header("Authorization", "Bearer " + tokenAdmin))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].clientSecretPreenchido").value(true))
            .andReturn().getResponse().getContentAsString();

        if (respostaGet.contains(SEGREDO)) {
            throw new AssertionError("Resposta expôs client secret em claro.");
        }

        String cifrado = jdbcTemplate.queryForObject(
            "SELECT client_secret_cifrado FROM configuracao_pix_bb WHERE ambiente = 'SANDBOX' LIMIT 1",
            String.class
        );
        if (cifrado.contains(SEGREDO)) {
            throw new AssertionError("Segredo gravado em claro no banco.");
        }

        criarUsuarioFiscal();
        String tokenFiscal = login(TENANT_DEMO, "fiscal", "Demo@123");

        mockMvc.perform(get("/api/plataforma/configuracao-pix")
                .header("Authorization", "Bearer " + tokenFiscal))
            .andExpect(status().isForbidden());
    }

    @Test
    void deveAtivarApenasUltimaConfiguracao() throws Exception {
        String token = login(TENANT_DEMO, "admin", "Demo@123");

        mockMvc.perform(put("/api/plataforma/configuracao-pix/SANDBOX")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpoConfig(true)))
            .andExpect(status().isOk());

        mockMvc.perform(put("/api/plataforma/configuracao-pix/PRODUCAO")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpoConfig(true).replace("client-demo", "client-prod")))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/plataforma/configuracao-pix")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.ambiente=='SANDBOX')].ativo[0]").value(false))
            .andExpect(jsonPath("$[?(@.ambiente=='PRODUCAO')].ativo[0]").value(true));
    }

    @Test
    void adminDeUmTenantNaoVeConfiguracaoDeOutroTenant() throws Exception {
        String tokenDemo = login(TENANT_DEMO, "admin", "Demo@123");
        mockMvc.perform(put("/api/plataforma/configuracao-pix/SANDBOX")
                .header("Authorization", "Bearer " + tokenDemo)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpoConfig(true, "client-tenant-demo", SEGREDO)))
            .andExpect(status().isOk());

        JsonNode tenantB = criarTenantViaPlataforma();
        String tokenTenantB = login(
            tenantB.get("slug").asText(),
            tenantB.get("usuarioAdminLogin").asText(),
            tenantB.get("senhaTemporaria").asText()
        );

        mockMvc.perform(put("/api/plataforma/configuracao-pix/SANDBOX")
                .header("Authorization", "Bearer " + tokenTenantB)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpoConfig(true, "client-tenant-b", "segredo-tenant-b")))
            .andExpect(status().isOk());

        String respostaTenantB = mockMvc.perform(get("/api/plataforma/configuracao-pix")
                .header("Authorization", "Bearer " + tokenTenantB))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        assertThat(respostaTenantB).contains("client-tenant-b");
        assertThat(respostaTenantB).doesNotContain("client-tenant-demo");
        assertThat(respostaTenantB).doesNotContain(SEGREDO);

        String respostaDemo = mockMvc.perform(get("/api/plataforma/configuracao-pix")
                .header("Authorization", "Bearer " + tokenDemo))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        assertThat(respostaDemo).contains("client-tenant-demo");
        assertThat(respostaDemo).doesNotContain("client-tenant-b");
    }

    @Test
    void naoDeveRegistrarSegredoEmLog() throws Exception {
        Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        root.addAppender(appender);

        try {
            String tokenAdmin = login(TENANT_DEMO, "admin", "Demo@123");
            mockMvc.perform(put("/api/plataforma/configuracao-pix/HOMOLOGACAO")
                    .header("Authorization", "Bearer " + tokenAdmin)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(corpoConfig(true, "client-log-test", SEGREDO)))
                .andExpect(status().isOk());

            String logs = appender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .reduce("", (a, b) -> a + b);

            assertThat(logs).doesNotContain(SEGREDO);
        } finally {
            root.detachAppender(appender);
        }
    }

    private JsonNode criarTenantViaPlataforma() throws Exception {
        String tokenPlataforma = login(TENANT_PLATAFORMA, "plataforma-admin", "Demo@123");
        String slug = "cidade-seg-" + System.nanoTime();
        String resposta = mockMvc.perform(post("/api/admin/tenants")
                .header("Authorization", "Bearer " + tokenPlataforma)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "slug": "%s",
                      "nome": "Prefeitura Seguranca Teste",
                      "uf": "SP",
                      "tipoEntidade": "PREFEITURA",
                      "modulosAtivos": ["cadastro", "iss"],
                      "loginAdminInicial": "admin",
                      "emailAdminInicial": "admin@%s.local"
                    }
                    """.formatted(slug, slug)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(resposta);
    }

    private void criarUsuarioFiscal() {
        jdbcTemplate.update("""
            INSERT INTO usuario (id, tenant_id, login, email, senha_hash, mfa_habilitado, ativo)
            SELECT gen_random_uuid(), t.id, 'fiscal', 'fiscal@demo.gov.br',
                   '$2a$10$sSqLb.JalB61zOLrMb/9wuqqEdjKQgkhuCqYZmhuSDizy0hCd3S7K', false, true
            FROM tenant t WHERE t.slug = 'demo'
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.update("""
            INSERT INTO usuario_papel (usuario_id, papel_id)
            SELECT u.id, p.id
            FROM usuario u, papel p
            WHERE u.login = 'fiscal' AND p.nome = 'FISCAL'
            ON CONFLICT DO NOTHING
            """);
    }

    private String login(String tenantSlug, String usuario, String senha) throws Exception {
        String corpo = mockMvc.perform(post("/api/auth/login")
                .header("X-Tenant-Slug", tenantSlug)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"%s\",\"senha\":\"%s\"}".formatted(usuario, senha)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("tokens").get("accessToken").asText();
    }

    private String corpoConfig(boolean ativo) {
        return corpoConfig(ativo, "client-demo", SEGREDO);
    }

    private String corpoConfig(boolean ativo, String clientId, String clientSecret) {
        return """
            {
              "ativo": %s,
              "clientId": "%s",
              "clientSecret": "%s",
              "developerApplicationKey": "dev-key-demo",
              "escopos": "pix.arrecadacao-requisicao pix.arrecadacao-info",
              "numeroConvenio": "123456",
              "chavePix": "00000000000000000000000000000000000000000000",
              "indicadorCodigoBarras": "N"
            }
            """.formatted(ativo, clientId, clientSecret);
    }
}
