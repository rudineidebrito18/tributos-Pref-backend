package br.com.tributos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.tributos.support.AbstractIntegrationTest;

import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AtividadeControllerTest extends AbstractIntegrationTest {

    private static final String TENANT_SLUG = "demo";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveSalvarListarEFiltrarPorIsServico() throws Exception {
        String token = login();

        String corpo = mockMvc.perform(post("/api/iss/atividades")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "codigo": "9999-9/99",
                      "descricao": "Atividade não serviço teste",
                      "ativo": true,
                      "isServico": false,
                      "observacao": "Observação teste"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.isServico").value(false))
            .andExpect(jsonPath("$.observacao").value("Observação teste"))
            .andReturn().getResponse().getContentAsString();

        String atividadeId = objectMapper.readTree(corpo).get("id").asText();

        mockMvc.perform(get("/api/iss/atividades/" + atividadeId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isServico").value(false));

        mockMvc.perform(get("/api/iss/atividades")
                .header("Authorization", "Bearer " + token)
                .param("isServico", "true"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*].id", not(hasItem(atividadeId))));

        mockMvc.perform(get("/api/iss/atividades")
                .header("Authorization", "Bearer " + token)
                .param("isServico", "false"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*].id", hasItem(atividadeId)));
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
