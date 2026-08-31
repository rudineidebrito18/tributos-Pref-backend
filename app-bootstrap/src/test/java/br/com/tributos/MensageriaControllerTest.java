package br.com.tributos;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import br.com.tributos.support.AbstractIntegrationTest;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Fluxo E2.5: envio entre usuários, caixas ENVIADAS/ENTRADA/ARQUIVADAS e bloqueio IDOR.
 */
class MensageriaControllerTest extends AbstractIntegrationTest {

    private static final String TENANT_SLUG = "demo";
    private static final String SENHA = "Demo@123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private UUID usuarioAId;
    private UUID usuarioBId;
    private UUID usuarioCId;

    @BeforeEach
    void prepararUsuarios() {
        jdbcTemplate.update("""
            DELETE FROM mensagem_interna_destinatario
             WHERE tenant_id = (SELECT id FROM tenant WHERE slug = 'demo')
            """);
        jdbcTemplate.update("""
            DELETE FROM mensagem_interna
             WHERE tenant_id = (SELECT id FROM tenant WHERE slug = 'demo')
            """);

        criarUsuarioSeNecessario("usuario-a", "usuario-a@demo.gov.br", "ATENDENTE");
        criarUsuarioSeNecessario("usuario-b", "usuario-b@demo.gov.br", "FISCAL");
        criarUsuarioSeNecessario("usuario-c", "usuario-c@demo.gov.br", "FISCAL");

        usuarioAId = buscarUsuarioId("usuario-a");
        usuarioBId = buscarUsuarioId("usuario-b");
        usuarioCId = buscarUsuarioId("usuario-c");
    }

    @Test
    void deveEnviarArquivarEProtegerAcessoPorParticipante() throws Exception {
        String tokenA = login("usuario-a");
        String tokenB = login("usuario-b");
        String tokenC = login("usuario-c");

        String respostaEnvio = mockMvc.perform(post("/api/plataforma/mensagens")
                .header("Authorization", "Bearer " + tokenA)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "assunto": "Reunião fiscal",
                      "corpo": "Confirmar pauta de amanhã.",
                      "destinatarioIds": ["%s"]
                    }
                    """.formatted(usuarioBId)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.assunto").value("Reunião fiscal"))
            .andReturn().getResponse().getContentAsString();

        UUID mensagemId = UUID.fromString(objectMapper.readTree(respostaEnvio).get("id").asText());

        mockMvc.perform(get("/api/plataforma/mensagens")
                .header("Authorization", "Bearer " + tokenA)
                .param("caixa", "ENVIADAS"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].assunto").value("Reunião fiscal"))
            .andExpect(jsonPath("$.content[0].usuario").value("usuario-b"));

        mockMvc.perform(get("/api/plataforma/mensagens")
                .header("Authorization", "Bearer " + tokenB)
                .param("caixa", "ENTRADA"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].assunto").value("Reunião fiscal"))
            .andExpect(jsonPath("$.content[0].usuario").value("usuario-a"));

        mockMvc.perform(post("/api/plataforma/mensagens/{id}/arquivar", mensagemId)
                .header("Authorization", "Bearer " + tokenB))
            .andExpect(status().isNoContent());

        String entradaAposArquivar = mockMvc.perform(get("/api/plataforma/mensagens")
                .header("Authorization", "Bearer " + tokenB)
                .param("caixa", "ENTRADA"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        assertThat(entradaAposArquivar).doesNotContain(mensagemId.toString());

        mockMvc.perform(get("/api/plataforma/mensagens")
                .header("Authorization", "Bearer " + tokenB)
                .param("caixa", "ARQUIVADAS"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(mensagemId.toString()));

        mockMvc.perform(get("/api/plataforma/mensagens/{id}", mensagemId)
                .header("Authorization", "Bearer " + tokenC))
            .andExpect(status().isNotFound());
    }

    private void criarUsuarioSeNecessario(String login, String email, String papel) {
        jdbcTemplate.update("""
            INSERT INTO usuario (id, tenant_id, login, email, senha_hash, mfa_habilitado, ativo)
            SELECT gen_random_uuid(), t.id, ?, ?,
                   '$2a$10$sSqLb.JalB61zOLrMb/9wuqqEdjKQgkhuCqYZmhuSDizy0hCd3S7K', false, true
            FROM tenant t WHERE t.slug = 'demo'
            ON CONFLICT DO NOTHING
            """, login, email);

        jdbcTemplate.update("""
            INSERT INTO usuario_papel (usuario_id, papel_id)
            SELECT u.id, p.id
            FROM usuario u, papel p
            WHERE u.login = ? AND u.tenant_id = (SELECT id FROM tenant WHERE slug = 'demo') AND p.nome = ?
            ON CONFLICT DO NOTHING
            """, login, papel);
    }

    private UUID buscarUsuarioId(String login) {
        return jdbcTemplate.queryForObject("""
            SELECT u.id FROM usuario u
            JOIN tenant t ON t.id = u.tenant_id
            WHERE t.slug = 'demo' AND u.login = ?
            """, UUID.class, login);
    }

    private String login(String usuario) throws Exception {
        String corpo = mockMvc.perform(post("/api/auth/login")
                .header("X-Tenant-Slug", TENANT_SLUG)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"" + usuario + "\",\"senha\":\"" + SENHA + "\"}"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("tokens").get("accessToken").asText();
    }
}
