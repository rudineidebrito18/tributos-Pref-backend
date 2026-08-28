package br.com.tributos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Critério de aceite Sprint 4: emitir nota fiscal, listar e cancelar.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Testcontainers
class NotaFiscalControllerTest {

    private static final String TENANT_SLUG = "demo";
    private static final String TIPO_CONTRIBUINTE_ID = "b0000001-0000-4000-8000-000000000001";
    private static final String SITUACAO_ATIVA_ID = "c0000001-0000-4000-8000-000000000001";
    private static final String REGIME_SIMPLES_ID = "d0000001-0000-4000-8000-000000000001";
    private static final String STATUS_EM_ANALISE_ID = "a0000001-0000-4000-8000-000000000002";
    private static final String STATUS_APROVADO_ID = "a0000001-0000-4000-8000-000000000003";
    private static final String SERVICO_ID = "e0000002-0000-4000-8000-000000000002";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveEmitirListarECancelarNotaFiscal() throws Exception {
        String token = login();
        String pessoaContribuinteId = cadastrarPessoaJuridica(token, "11.444.777/0001-61", "Empresa NFS-e Teste");
        String contribuinteId = cadastrarEAprovarCredenciamento(token, pessoaContribuinteId);
        String pessoaTomadorId = cadastrarPessoaFisica(token, "390.533.447-05", "João Tomador NFS-e");

        String corpoTomador = mockMvc.perform(post("/api/iss/tomadores")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"pessoaId\": \"%s\"}".formatted(pessoaTomadorId)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        String tomadorId = objectMapper.readTree(corpoTomador).get("id").asText();

        String corpoNota = mockMvc.perform(post("/api/iss/notas-fiscais/emitir")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "contribuinteId": "%s",
                      "tomadorId": "%s",
                      "servicoId": "%s",
                      "competencia": "2024-06-01",
                      "valorServico": 10000,
                      "valorDeducoes": 0,
                      "receitaBrutaAcumulada12Meses": 200000
                    }
                    """.formatted(contribuinteId, tomadorId, SERVICO_ID)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("EMITIDA"))
            .andExpect(jsonPath("$.numero").value(1))
            .andReturn().getResponse().getContentAsString();

        String notaId = objectMapper.readTree(corpoNota).get("id").asText();

        mockMvc.perform(get("/api/iss/notas-fiscais")
                .header("Authorization", "Bearer " + token)
                .param("contribuinteId", contribuinteId)
                .param("competencia", "2024-06")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].status").value("EMITIDA"));

        mockMvc.perform(post("/api/iss/notas-fiscais/%s/cancelar".formatted(notaId))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"motivo\": \"Erro nos dados do tomador.\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CANCELADA"));
    }

    private String cadastrarEAprovarCredenciamento(String token, String pessoaId) throws Exception {
        String corpoContribuinte = mockMvc.perform(post("/api/iss/contribuintes")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "pessoaId": "%s",
                      "inscricaoMunicipal": "654321",
                      "tipoContribuinteId": "%s",
                      "situacaoCadastralId": "%s",
                      "regimeTributarioId": "%s"
                    }
                    """.formatted(pessoaId, TIPO_CONTRIBUINTE_ID, SITUACAO_ATIVA_ID, REGIME_SIMPLES_ID)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        String contribuinteId = objectMapper.readTree(corpoContribuinte).get("id").asText();

        String corpoSolicitacao = mockMvc.perform(post("/api/iss/credenciamento/solicitar")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"contribuinteId\": \"%s\"}".formatted(contribuinteId)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.statusId").value(STATUS_EM_ANALISE_ID))
            .andReturn().getResponse().getContentAsString();

        String solicitacaoId = objectMapper.readTree(corpoSolicitacao).get("id").asText();

        mockMvc.perform(post("/api/iss/credenciamento/solicitacoes/%s/aprovar".formatted(solicitacaoId))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"observacao\": \"Documentação conferida.\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statusId").value(STATUS_APROVADO_ID));

        return contribuinteId;
    }

    private String cadastrarPessoaJuridica(String token, String cpfCnpj, String nome) throws Exception {
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
                      "tipoPessoa": "PJ",
                      "cpfCnpj": "%s",
                      "nome": "%s",
                      "razaoSocial": "%s Ltda",
                      "ativo": true,
                      "enderecos": [{
                        "cep": "01310100",
                        "logradouro": "Av Paulista",
                        "numero": "200",
                        "bairro": "Bela Vista",
                        "cidadeId": "%s",
                        "principal": true
                      }]
                    }
                    """.formatted(cpfCnpj, nome, nome, cidadeId)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(corpo).get("id").asText();
    }

    private String cadastrarPessoaFisica(String token, String cpfCnpj, String nome) throws Exception {
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
                      "cpfCnpj": "%s",
                      "nome": "%s",
                      "ativo": true,
                      "enderecos": [{
                        "cep": "01310100",
                        "logradouro": "Rua Augusta",
                        "numero": "500",
                        "bairro": "Consolação",
                        "cidadeId": "%s",
                        "principal": true
                      }]
                    }
                    """.formatted(cpfCnpj, nome, cidadeId)))
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
