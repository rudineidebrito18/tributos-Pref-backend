package br.com.tributos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Critério de aceite do Sprint 1: pessoa cadastrada com endereço e documento anexado.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Testcontainers
class DocumentoControllerTest {

    private static final String TENANT_SLUG = "demo";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveAnexarListarEBaixarDocumentoDaPessoa() throws Exception {
        String token = login();
        String pessoaId = cadastrarPessoaFisica(token);

        MockMultipartFile arquivo = new MockMultipartFile(
            "arquivo",
            "rg.pdf",
            "application/pdf",
            "%PDF-1.4 sprint1".getBytes()
        );

        mockMvc.perform(multipart("/api/cadastro/pessoas/{pessoaId}/documentos", pessoaId)
                .file(arquivo)
                .param("tipo", "RG")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nomeArquivo").value("rg.pdf"))
            .andExpect(jsonPath("$.tipo").value("RG"));

        mockMvc.perform(get("/api/cadastro/pessoas/{pessoaId}/documentos", pessoaId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].nomeArquivo").value("rg.pdf"));

        String documentoId = objectMapper.readTree(
            mockMvc.perform(get("/api/cadastro/pessoas/{pessoaId}/documentos", pessoaId)
                    .header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString()
        ).get(0).get("id").asText();

        mockMvc.perform(get("/api/cadastro/pessoas/{pessoaId}/documentos/{documentoId}/download", pessoaId, documentoId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Disposition", "attachment; filename=\"rg.pdf\""));
    }

    private String cadastrarPessoaFisica(String token) throws Exception {
        String cidadeId = objectMapper.readTree(
            mockMvc.perform(get("/api/cadastro/territorio/cidades?uf=SP")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()
        ).get(0).get("id").asText();

        String corpo = mockMvc.perform(post("/api/cadastro/pessoas")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "tipoPessoa": "PF",
                      "cpfCnpj": "529.982.247-25",
                      "nome": "Maria da Silva",
                      "email": "maria@email.com",
                      "ativo": true,
                      "enderecos": [{
                        "cep": "01310100",
                        "logradouro": "Avenida Paulista",
                        "numero": "1000",
                        "bairro": "Bela Vista",
                        "cidadeId": "%s",
                        "principal": true
                      }]
                    }
                    """.formatted(cidadeId)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(corpo).get("id").asText();
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
