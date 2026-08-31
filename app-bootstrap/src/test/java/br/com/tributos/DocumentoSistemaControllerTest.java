package br.com.tributos;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import br.com.tributos.support.AbstractIntegrationTest;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DocumentoSistemaControllerTest extends AbstractIntegrationTest {

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
            DELETE FROM documento_compartilhamento
             WHERE tenant_id = (SELECT id FROM tenant WHERE slug = 'demo')
            """);
        jdbcTemplate.update("""
            DELETE FROM documento
             WHERE tenant_id = (SELECT id FROM tenant WHERE slug = 'demo')
               AND pessoa_id IS NULL
               AND tipo = 'SISTEMA'
            """);
        jdbcTemplate.update("""
            DELETE FROM documento_categoria
             WHERE tenant_id = (SELECT id FROM tenant WHERE slug = 'demo')
            """);

        criarUsuarioSeNecessario("usuario-a", "usuario-a@demo.gov.br", "FISCAL");
        criarUsuarioSeNecessario("usuario-b", "usuario-b@demo.gov.br", "FISCAL");
        criarUsuarioSeNecessario("usuario-c", "usuario-c@demo.gov.br", "FISCAL");

        usuarioAId = buscarUsuarioId("usuario-a");
        usuarioBId = buscarUsuarioId("usuario-b");
        usuarioCId = buscarUsuarioId("usuario-c");
    }

    @Test
    void deveCompartilharDocumentoEntreUsuarios() throws Exception {
        String tokenA = login("usuario-a");
        String tokenB = login("usuario-b");
        String tokenC = login("usuario-c");

        String categoriaId = criarCategoria(tokenA, "Normas Internas");
        String documentoId = anexarDocumento(tokenA, categoriaId, "Manual Operacional");

        mockMvc.perform(post("/api/cadastro/documentos-sistema/{id}/compartilhar", documentoId)
                .header("Authorization", "Bearer " + tokenA)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"usuarioId\": \"" + usuarioBId + "\"}"))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/cadastro/documentos-sistema/compartilhados-comigo")
                .header("Authorization", "Bearer " + tokenB))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].titulo").value("Manual Operacional"))
            .andExpect(jsonPath("$.content[0].categoriaNome").value("Normas Internas"));

        mockMvc.perform(get("/api/cadastro/documentos-sistema/compartilhados-comigo")
                .header("Authorization", "Bearer " + tokenC))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(0)));

        mockMvc.perform(get("/api/cadastro/documentos-sistema/{id}/download", documentoId)
                .header("Authorization", "Bearer " + tokenB))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/cadastro/documentos-sistema/{id}/download", documentoId)
                .header("Authorization", "Bearer " + tokenC))
            .andExpect(status().isNotFound());
    }

    @Test
    void deveFiltrarDocumentosSistemaPorTitulo() throws Exception {
        String token = login("admin");
        String categoriaId = criarCategoria(token, "Geral");

        anexarDocumento(token, categoriaId, "Relatório Anual");
        anexarDocumento(token, categoriaId, "Circular Interna");

        mockMvc.perform(get("/api/cadastro/documentos-sistema")
                .header("Authorization", "Bearer " + token)
                .param("titulo", "Circular"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].titulo").value("Circular Interna"));
    }

    private String criarCategoria(String token, String nome) throws Exception {
        String corpo = mockMvc.perform(post("/api/cadastro/documento-categorias")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\": \"" + nome + "\"}"))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("id").asText();
    }

    private String anexarDocumento(String token, String categoriaId, String titulo) throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile(
            "arquivo",
            "manual.pdf",
            "application/pdf",
            "%PDF-1.4 teste".getBytes()
        );

        String corpo = mockMvc.perform(multipart("/api/cadastro/documentos-sistema")
                .file(arquivo)
                .param("titulo", titulo)
                .param("categoriaId", categoriaId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.titulo").value(titulo))
            .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(corpo).get("id").asText();
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
