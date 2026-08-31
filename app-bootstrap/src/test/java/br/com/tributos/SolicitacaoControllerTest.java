package br.com.tributos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import br.com.tributos.support.AbstractIntegrationTest;

import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SolicitacaoControllerTest extends AbstractIntegrationTest {

    private static final String TENANT_SLUG = "demo";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void deveAbrirListarEAlterarStatusGerandoMensagemInterna() throws Exception {
        String token = login();

        String adminId = jdbcTemplate.queryForObject(
            "SELECT id FROM usuario WHERE login = 'admin' AND tenant_id = (SELECT id FROM tenant WHERE slug = 'demo')",
            String.class
        );

        String tipoId = objectMapper.readTree(mockMvc.perform(post("/api/iss/tipos-solicitacao")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"descricao":"Teste protocolo","usuarioNotificarId":"%s","ativo":true}
                    """.formatted(adminId)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString()).get("id").asText();

        String statusAbertoId = objectMapper.readTree(mockMvc.perform(post("/api/iss/status-solicitacao")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"descricao\":\"Aberto\",\"ativo\":true}"))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString()).get("id").asText();

        String statusFechadoId = objectMapper.readTree(mockMvc.perform(post("/api/iss/status-solicitacao")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"descricao\":\"Fechado\",\"ativo\":true}"))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString()).get("id").asText();

        String corpo = mockMvc.perform(post("/api/iss/solicitacoes")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "tipoSolicitacaoId": "%s",
                      "statusSolicitacaoId": "%s",
                      "descricao": "Preciso alterar endereço cadastral",
                      "dataHora": "2026-08-31T12:00:00Z"
                    }
                    """.formatted(tipoId, statusAbertoId)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.descricao").value("Preciso alterar endereço cadastral"))
            .andReturn().getResponse().getContentAsString();

        String solicitacaoId = objectMapper.readTree(corpo).get("id").asText();

        mockMvc.perform(get("/api/iss/solicitacoes")
                .header("Authorization", "Bearer " + token)
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[?(@.id=='" + solicitacaoId + "')].usuario").value("admin"));

        mockMvc.perform(patch("/api/iss/solicitacoes/%s/status".formatted(solicitacaoId))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"statusSolicitacaoId\": \"%s\"}".formatted(statusFechadoId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statusSolicitacaoId").value(statusFechadoId));

        Integer mensagens = jdbcTemplate.queryForObject(
            """
            SELECT COUNT(*) FROM mensagem_interna_destinatario d
            JOIN mensagem_interna m ON m.id = d.mensagem_id
            WHERE d.destinatario_id = ?::uuid
              AND m.assunto LIKE 'Solicitação atualizada:%'
            """,
            Integer.class,
            adminId
        );
        assertThat(mensagens).isGreaterThanOrEqualTo(1);
    }

    private String login() throws Exception {
        String corpo = mockMvc.perform(post("/api/auth/login")
                .header("X-Tenant-Slug", TENANT_SLUG)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"admin\",\"senha\":\"Demo@123\"}"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("tokens").get("accessToken").asText();
    }
}
