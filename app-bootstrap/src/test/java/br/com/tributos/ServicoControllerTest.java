package br.com.tributos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import br.com.tributos.support.AbstractIntegrationTest;

import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ServicoControllerTest extends AbstractIntegrationTest {

    private static final String TENANT_SLUG = "demo";
    private static final String SERVICO_ID = "e0000002-0000-4000-8000-000000000002";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void deveListarServicoComGrupoBackfillEAtualizarCodigoNbs() throws Exception {
        String token = login();

        String grupoId = jdbcTemplate.queryForObject(
            """
            SELECT g.id FROM iss_grupo_servico g
            JOIN tenant t ON t.id = g.tenant_id
            WHERE t.slug = 'demo' AND g.codigo = '17'
            """,
            String.class
        );

        mockMvc.perform(get("/api/iss/servicos/" + SERVICO_ID)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.grupoServicoId").value(grupoId));

        mockMvc.perform(put("/api/iss/servicos/" + SERVICO_ID)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "codigoLc116": "17.01",
                      "descricao": "Assessoria ou consultoria de qualquer natureza",
                      "aliquotaMinima": 2.0,
                      "aliquotaMaxima": 5.0,
                      "ativo": true,
                      "grupoServicoId": "%s",
                      "codigoNbs": "1.0101.00"
                    }
                    """.formatted(grupoId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.codigoNbs").value("1.0101.00"));

        mockMvc.perform(post("/api/iss/servicos")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "codigoLc116": "99.99",
                      "descricao": "Serviço teste E4.3",
                      "ativo": true,
                      "grupoServicoId": "%s",
                      "codigoTributacaoNacional": "010701"
                    }
                    """.formatted(grupoId)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.codigoTributacaoNacional").value("010701"));
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
