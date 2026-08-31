package br.com.tributos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import br.com.tributos.support.AbstractIntegrationTest;

import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TerritorioBairroControllerTest extends AbstractIntegrationTest {

    private static final String TENANT_SLUG = "demo";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void deveCadastrarBairroComAtributosFiscais() throws Exception {
        String token = loginAdmin();
        String cidadeId = obterPrimeiraCidadeId(token);

        mockMvc.perform(post("/api/cadastro/bairros")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "cidadeId": "%s",
                      "nome": "Centro Histórico",
                      "valorTerreno": 850.50
                    }
                    """.formatted(cidadeId)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nome").value("Centro Histórico"))
            .andExpect(jsonPath("$.valorTerreno").value(850.50));

        mockMvc.perform(get("/api/cadastro/bairros")
                .header("Authorization", "Bearer " + token)
                .param("cidadeId", cidadeId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(org.hamcrest.Matchers.greaterThan(0))));
    }

    @Test
    void atendenteNaoPodeCadastrarBairro() throws Exception {
        String tokenAdmin = loginAdmin();
        String cidadeId = obterPrimeiraCidadeId(tokenAdmin);
        criarUsuarioAtendente();
        String tokenAtendente = login("atendente", "Demo@123");

        mockMvc.perform(post("/api/cadastro/bairros")
                .header("Authorization", "Bearer " + tokenAtendente)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "cidadeId": "%s",
                      "nome": "Jardim Teste"
                    }
                    """.formatted(cidadeId)))
            .andExpect(status().isForbidden());
    }

    @Test
    void deveCadastrarLogradouroComCep() throws Exception {
        String token = loginAdmin();
        String cidadeId = obterPrimeiraCidadeId(token);

        mockMvc.perform(post("/api/cadastro/logradouros")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "cidadeId": "%s",
                      "nome": "Rua das Flores",
                      "cep": "01310-100"
                    }
                    """.formatted(cidadeId)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nome").value("Rua das Flores"))
            .andExpect(jsonPath("$.cep").value("01310100"));
    }

    private String obterPrimeiraCidadeId(String token) throws Exception {
        String resposta = mockMvc.perform(get("/api/cadastro/territorio/cidades?uf=SP")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(resposta).get(0).get("id").asText();
    }

    private String loginAdmin() throws Exception {
        return login("admin", "Demo@123");
    }

    private void criarUsuarioAtendente() {
        jdbcTemplate.update("""
            INSERT INTO usuario (id, tenant_id, login, email, senha_hash, mfa_habilitado, ativo)
            SELECT gen_random_uuid(), t.id, 'atendente', 'atendente@demo.gov.br',
                   '$2a$10$sSqLb.JalB61zOLrMb/9wuqqEdjKQgkhuCqYZmhuSDizy0hCd3S7K', false, true
            FROM tenant t WHERE t.slug = 'demo'
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.update("""
            INSERT INTO usuario_papel (usuario_id, papel_id)
            SELECT u.id, p.id
            FROM usuario u, papel p
            WHERE u.login = 'atendente' AND u.tenant_id = (SELECT id FROM tenant WHERE slug = 'demo')
              AND p.nome = 'ATENDENTE'
            ON CONFLICT DO NOTHING
            """);
    }

    private String login(String usuario, String senha) throws Exception {
        String corpo = mockMvc.perform(post("/api/auth/login")
                .header("X-Tenant-Slug", TENANT_SLUG)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"%s\",\"senha\":\"%s\"}".formatted(usuario, senha)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("tokens").get("accessToken").asText();
    }
}
