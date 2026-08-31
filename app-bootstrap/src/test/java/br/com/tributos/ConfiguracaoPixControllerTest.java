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

import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Testcontainers
class ConfiguracaoPixControllerTest {

    private static final String TENANT_SLUG = "demo";
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
        String tokenAdmin = login("admin", "Demo@123");

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
        String tokenFiscal = login("fiscal", "Demo@123");

        mockMvc.perform(get("/api/plataforma/configuracao-pix")
                .header("Authorization", "Bearer " + tokenFiscal))
            .andExpect(status().isForbidden());
    }

    @Test
    void deveAtivarApenasUltimaConfiguracao() throws Exception {
        String token = login("admin", "Demo@123");

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

    private String login(String usuario, String senha) throws Exception {
        String corpo = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/auth/login")
                    .header("X-Tenant-Slug", TENANT_SLUG)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"usuario\":\"%s\",\"senha\":\"%s\"}".formatted(usuario, senha)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("tokens").get("accessToken").asText();
    }

    private String corpoConfig(boolean ativo) {
        return """
            {
              "ativo": %s,
              "clientId": "client-demo",
              "clientSecret": "%s",
              "developerApplicationKey": "dev-key-demo",
              "escopos": "pix.arrecadacao-requisicao pix.arrecadacao-info",
              "numeroConvenio": "123456",
              "chavePix": "00000000000000000000000000000000000000000000",
              "indicadorCodigoBarras": "N"
            }
            """.formatted(ativo, SEGREDO);
    }
}
