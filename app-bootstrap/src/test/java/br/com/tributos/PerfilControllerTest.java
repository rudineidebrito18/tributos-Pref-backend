package br.com.tributos;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import br.com.tributos.support.AbstractIntegrationTest;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PerfilControllerTest extends AbstractIntegrationTest {

    private static final String TENANT_SLUG = "demo";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void deveAtualizarNomeDoProprioPerfil() throws Exception {
        String token = login("admin");

        mockMvc.perform(put("/api/plataforma/perfil")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "nome": "Administrador Demo",
                      "login": "admin",
                      "email": "admin@demo.gov.br"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome").value("Administrador Demo"));

        mockMvc.perform(get("/api/plataforma/perfil")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome").value("Administrador Demo"));
    }

    @Test
    void deveRejeitarSenhasDivergentes() throws Exception {
        String token = login("admin");

        mockMvc.perform(put("/api/plataforma/perfil")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "nome": "Admin",
                      "login": "admin",
                      "email": "admin@demo.gov.br",
                      "password1": "Nova@123",
                      "password2": "Outra@456"
                    }
                    """))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.mensagem").value("As senhas informadas não conferem."));
    }

    @Test
    void naoDeveAlterarOutroUsuarioMesmoComUsuarioIdNoCorpo() throws Exception {
        criarUsuarioSeNecessario("usuario-b", "usuario-b@demo.gov.br");
        UUID outroUsuarioId = buscarUsuarioId("usuario-b");
        String tokenAdmin = login("admin");

        mockMvc.perform(put("/api/plataforma/perfil")
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "usuarioId": "%s",
                      "nome": "Invasor",
                      "login": "admin",
                      "email": "admin@demo.gov.br"
                    }
                    """.formatted(outroUsuarioId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome").value("Invasor"))
            .andExpect(jsonPath("$.login").value("admin"));

        String nomeOutro = jdbcTemplate.queryForObject(
            "SELECT COALESCE(nome, login) FROM usuario WHERE id = ?",
            String.class,
            outroUsuarioId
        );
        assertThat(nomeOutro).isNotEqualTo("Invasor");
    }

    private void criarUsuarioSeNecessario(String login, String email) {
        jdbcTemplate.update("""
            INSERT INTO usuario (id, tenant_id, login, email, senha_hash, mfa_habilitado, ativo)
            SELECT gen_random_uuid(), t.id, ?, ?,
                   '$2a$10$sSqLb.JalB61zOLrMb/9wuqqEdjKQgkhuCqYZmhuSDizy0hCd3S7K', false, true
            FROM tenant t WHERE t.slug = 'demo'
            ON CONFLICT DO NOTHING
            """, login, email);
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
                .content("{\"login\":\"" + usuario + "\",\"senha\":\"Demo@123\"}"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("tokens").get("accessToken").asText();
    }
}
