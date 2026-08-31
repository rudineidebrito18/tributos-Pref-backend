package br.com.tributos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.tributos.support.AbstractIntegrationTest;

import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ApoioIssControllerTest extends AbstractIntegrationTest {

    private static final String TENANT_SLUG = "demo";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCadastrarERecuperarSituacaoCnd() throws Exception {
        String token = loginAdmin();

        mockMvc.perform(post("/api/iss/situacoes-cnd")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "descricao": "Regular",
                      "titulo": "Situação Regular",
                      "ativo": true
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.descricao").value("Regular"))
            .andExpect(jsonPath("$.titulo").value("Situação Regular"))
            .andExpect(jsonPath("$.ativo").value(true));

        mockMvc.perform(get("/api/iss/situacoes-cnd")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(org.hamcrest.Matchers.greaterThan(0))));
    }

    @Test
    void deveCadastrarERecuperarTipoSolicitacao() throws Exception {
        String token = loginAdmin();

        mockMvc.perform(post("/api/iss/tipos-solicitacao")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "descricao": "Alteração cadastral",
                      "ativo": true
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.descricao").value("Alteração cadastral"))
            .andExpect(jsonPath("$.ativo").value(true));

        mockMvc.perform(get("/api/iss/tipos-solicitacao")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(org.hamcrest.Matchers.greaterThan(0))));
    }

    @Test
    void deveCadastrarERecuperarStatusSolicitacao() throws Exception {
        String token = loginAdmin();

        mockMvc.perform(post("/api/iss/status-solicitacao")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "descricao": "Em análise",
                      "ativo": true
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.descricao").value("Em análise"))
            .andExpect(jsonPath("$.ativo").value(true));

        mockMvc.perform(get("/api/iss/status-solicitacao")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(org.hamcrest.Matchers.greaterThan(0))));
    }

    @Test
    void deveCadastrarERecuperarLocalIncidencia() throws Exception {
        String token = loginAdmin();

        mockMvc.perform(post("/api/iss/locais-incidencia")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "descricao": "LOCAL_TESTE_INTEGRACAO",
                      "ativo": true
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.descricao").value("LOCAL_TESTE_INTEGRACAO"))
            .andExpect(jsonPath("$.ativo").value(true));

        mockMvc.perform(get("/api/iss/locais-incidencia")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(org.hamcrest.Matchers.greaterThan(0))));
    }

    @Test
    void deveCadastrarERecuperarGrupoServico() throws Exception {
        String token = loginAdmin();

        mockMvc.perform(post("/api/iss/grupos-servico")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "codigo": "99",
                      "descricao": "Grupo de teste integração"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.codigo").value("99"))
            .andExpect(jsonPath("$.descricao").value("Grupo de teste integração"));

        mockMvc.perform(get("/api/iss/grupos-servico")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(org.hamcrest.Matchers.greaterThan(0))));
    }

    private String loginAdmin() throws Exception {
        return login("admin", "Demo@123");
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
