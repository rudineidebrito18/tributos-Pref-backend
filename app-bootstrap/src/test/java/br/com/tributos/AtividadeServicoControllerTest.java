package br.com.tributos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import br.com.tributos.support.AbstractIntegrationTest;

import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AtividadeServicoControllerTest extends AbstractIntegrationTest {

    private static final String TENANT_SLUG = "demo";
    private static final String ATIVIDADE_ID = "e0000001-0000-4000-8000-000000000001";
    private static final String SERVICO_ID = "e0000002-0000-4000-8000-000000000002";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String tokenAdmin;
    private String localIncidenciaId;

    @BeforeEach
    void preparar() throws Exception {
        tokenAdmin = login("admin", "Demo@123");
        criarUsuarioFiscal();
        localIncidenciaId = jdbcTemplate.queryForObject(
            """
            SELECT l.id FROM iss_local_incidencia l
            JOIN tenant t ON t.id = l.tenant_id
            WHERE t.slug = 'demo' AND l.descricao = 'ESTABELECIMENTO_PRESTADOR'
            """,
            String.class
        );
        jdbcTemplate.update(
            "DELETE FROM iss_atividade_servico WHERE atividade_id = ? AND servico_id = ?",
            java.util.UUID.fromString(ATIVIDADE_ID),
            java.util.UUID.fromString(SERVICO_ID)
        );
    }

    @Test
    void deveCriarParListarNaViewEAuditarAlteracaoAliquota() throws Exception {
        String corpo = mockMvc.perform(post("/api/iss/atividades-servicos")
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "atividadeId": "%s",
                      "servicoId": "%s",
                      "localIncidenciaId": "%s",
                      "aliquota": 3.5,
                      "tributavel": true,
                      "imune": false,
                      "deducao": true,
                      "substitutoTributario": false,
                      "retencaoFonte": true,
                      "observacao": "Par teste E4.5"
                    }
                    """.formatted(ATIVIDADE_ID, SERVICO_ID, localIncidenciaId)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.aliquota").value(3.5))
            .andExpect(jsonPath("$.retencaoFonte").value(true))
            .andReturn().getResponse().getContentAsString();

        String vinculoId = objectMapper.readTree(corpo).get("id").asText();

        mockMvc.perform(get("/api/iss/atividades-servicos/view")
                .header("Authorization", "Bearer " + tokenAdmin)
                .param("page", "0")
                .param("size", "50"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[*].id", hasItem(vinculoId)))
            .andExpect(jsonPath("$.content[?(@.id == '%s')].cnae".formatted(vinculoId)).value("6201-5/00"))
            .andExpect(jsonPath("$.content[?(@.id == '%s')].codigo".formatted(vinculoId)).value("17.01"))
            .andExpect(jsonPath("$.content[?(@.id == '%s')].servico".formatted(vinculoId)).exists())
            .andExpect(jsonPath("$.content[?(@.id == '%s')].aliquota".formatted(vinculoId)).value(3.5))
            .andExpect(jsonPath("$.content[?(@.id == '%s')].tributavel".formatted(vinculoId)).value(true))
            .andExpect(jsonPath("$.content[?(@.id == '%s')].deducao".formatted(vinculoId)).value(true))
            .andExpect(jsonPath("$.content[?(@.id == '%s')].retencao".formatted(vinculoId)).value(true))
            .andExpect(jsonPath("$.content[?(@.id == '%s')].incidencia".formatted(vinculoId)).value("ESTABELECIMENTO_PRESTADOR"));

        String tokenFiscal = login("fiscal", "Demo@123");
        mockMvc.perform(put("/api/iss/atividades-servicos/" + vinculoId)
                .header("Authorization", "Bearer " + tokenFiscal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpoAtualizacao(4.0)))
            .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/iss/atividades-servicos/" + vinculoId)
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpoAtualizacao(4.0)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aliquota").value(4.0));

        Integer registrosAuditoria = jdbcTemplate.queryForObject(
            """
            SELECT COUNT(*) FROM log_auditoria
            WHERE entidade = 'iss_atividade_servico'
              AND entidade_id = ?
              AND acao = 'ALTERAR_ALIQUOTA'
            """,
            Integer.class,
            vinculoId
        );
        org.assertj.core.api.Assertions.assertThat(registrosAuditoria).isGreaterThan(0);
    }

    private String corpoAtualizacao(double aliquota) {
        return """
            {
              "atividadeId": "%s",
              "servicoId": "%s",
              "localIncidenciaId": "%s",
              "aliquota": %s,
              "tributavel": true,
              "imune": false,
              "deducao": true,
              "substitutoTributario": false,
              "retencaoFonte": true
            }
            """.formatted(ATIVIDADE_ID, SERVICO_ID, localIncidenciaId, aliquota);
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
        String corpo = mockMvc.perform(post("/api/auth/login")
                .header("X-Tenant-Slug", TENANT_SLUG)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"%s\",\"senha\":\"%s\"}".formatted(usuario, senha)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("tokens").get("accessToken").asText();
    }
}
