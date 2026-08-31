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
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Critério de aceite Sprint 8: emitir nota fiscal gera guia automaticamente; PIX simulado confirma baixa.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Testcontainers
class GuiaArrecadacaoControllerTest {

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
    void deveGerarGuiaAutomaticamenteAoEmitirNotaEConfirmarPix() throws Exception {
        String token = login();
        String pessoaContribuinteId = cadastrarPessoaJuridica(token, "11.555.777/0001-61", "Empresa Financeiro Teste");
        String contribuinteId = cadastrarEAprovarCredenciamento(token, pessoaContribuinteId);
        String pessoaTomadorId = cadastrarPessoaFisica(token, "529.982.247-25", "Tomador Financeiro");

        String corpoTomador = mockMvc.perform(post("/api/iss/tomadores")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"pessoaId\": \"%s\"}".formatted(pessoaTomadorId)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        String tomadorId = objectMapper.readTree(corpoTomador).get("id").asText();

        mockMvc.perform(post("/api/iss/notas-fiscais/emitir")
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
            .andExpect(status().isCreated());

        String corpoListagem = mockMvc.perform(get("/api/financeiro/guias-arrecadacao")
                .header("Authorization", "Bearer " + token)
                .param("tipoTributo", "ISS")
                .param("contribuinteId", pessoaContribuinteId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].situacao").value("PENDENTE"))
            .andExpect(jsonPath("$.content[0].tipoTributo").value("ISS"))
            .andReturn().getResponse().getContentAsString();

        String guiaId = objectMapper.readTree(corpoListagem).get("content").get(0).get("id").asText();

        mockMvc.perform(post("/api/financeiro/guias-arrecadacao/" + guiaId + "/simular-pix")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pixTxid").exists());

        mockMvc.perform(get("/api/financeiro/guias-arrecadacao/" + guiaId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statusPix").value("ATIVA"))
            .andExpect(jsonPath("$.statusPixDescricao").value("ATIVA"));

        mockMvc.perform(post("/api/financeiro/guias-arrecadacao/" + guiaId + "/confirmar-pix")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.situacao").value("PAGA"))
            .andExpect(jsonPath("$.valorPago").exists())
            .andExpect(jsonPath("$.statusPix").value("CONCLUIDA"))
            .andExpect(jsonPath("$.formaPagamentoCodigo").value("PIX"));

        mockMvc.perform(get("/api/financeiro/guias-arrecadacao")
                .header("Authorization", "Bearer " + token)
                .param("statusPix", "CONCLUIDA")
                .param("formaPagamentoCodigo", "PIX")
                .param("contribuinteId", pessoaContribuinteId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].statusPix").value("CONCLUIDA"));
    }

    @Test
    void naoDevePermitirAlterarStatusPixOuValorPagoViaPutOuPatch() throws Exception {
        String token = login();
        String guiaId = criarGuiaPendente(token);

        mockMvc.perform(put("/api/financeiro/guias-arrecadacao/" + guiaId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"statusPix":"CONCLUIDA","valorPago":0.01,"situacao":"PAGA"}
                    """))
            .andExpect(status().is4xxClientError());

        mockMvc.perform(patch("/api/financeiro/guias-arrecadacao/" + guiaId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"statusPix":"CONCLUIDA","valorPago":0.01}
                    """))
            .andExpect(status().is4xxClientError());

        mockMvc.perform(get("/api/financeiro/guias-arrecadacao/" + guiaId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.situacao").value("PENDENTE"))
            .andExpect(jsonPath("$.statusPix").value(nullValue()))
            .andExpect(jsonPath("$.valorPago").value(nullValue()));
    }

    private String criarGuiaPendente(String token) throws Exception {
        String pessoaContribuinteId = cadastrarPessoaJuridica(token, "22.333.444/0001-81", "Empresa Seguranca Guia");
        String contribuinteId = cadastrarEAprovarCredenciamento(token, pessoaContribuinteId);
        String pessoaTomadorId = cadastrarPessoaFisica(token, "390.533.447-05", "Tomador Seguranca");

        String corpoTomador = mockMvc.perform(post("/api/iss/tomadores")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"pessoaId\": \"%s\"}".formatted(pessoaTomadorId)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        String tomadorId = objectMapper.readTree(corpoTomador).get("id").asText();

        mockMvc.perform(post("/api/iss/notas-fiscais/emitir")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "contribuinteId": "%s",
                      "tomadorId": "%s",
                      "servicoId": "%s",
                      "competencia": "2024-07-01",
                      "valorServico": 5000,
                      "valorDeducoes": 0,
                      "receitaBrutaAcumulada12Meses": 200000
                    }
                    """.formatted(contribuinteId, tomadorId, SERVICO_ID)))
            .andExpect(status().isCreated());

        String corpoListagem = mockMvc.perform(get("/api/financeiro/guias-arrecadacao")
                .header("Authorization", "Bearer " + token)
                .param("tipoTributo", "ISS")
                .param("contribuinteId", pessoaContribuinteId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(corpoListagem).get("content").get(0).get("id").asText();
    }

    private String login() throws Exception {
        String corpo = mockMvc.perform(post("/api/auth/login")
                .header("X-Tenant-Slug", TENANT_SLUG)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"login":"admin","senha":"Demo@123"}
                    """))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("tokens").get("accessToken").asText();
    }

    private String cadastrarPessoaJuridica(String token, String cnpj, String nome) throws Exception {
        String corpo = mockMvc.perform(post("/api/cadastro/pessoas")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"tipo":"JURIDICA","nome":"%s","cpfCnpj":"%s"}
                    """.formatted(nome, cnpj)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("id").asText();
    }

    private String cadastrarPessoaFisica(String token, String cpf, String nome) throws Exception {
        String corpo = mockMvc.perform(post("/api/cadastro/pessoas")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"tipo":"FISICA","nome":"%s","cpfCnpj":"%s"}
                    """.formatted(nome, cpf)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("id").asText();
    }

    private String cadastrarEAprovarCredenciamento(String token, String pessoaId) throws Exception {
        String corpoContrib = mockMvc.perform(post("/api/iss/contribuintes")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "pessoaId": "%s",
                      "tipoContribuinteId": "%s",
                      "situacaoFiscalId": "%s",
                      "regimeTributarioId": "%s",
                      "inscricaoMunicipal": "FIN-%s"
                    }
                    """.formatted(pessoaId, TIPO_CONTRIBUINTE_ID, SITUACAO_ATIVA_ID, REGIME_SIMPLES_ID, pessoaId.substring(0, 8))))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        String contribuinteId = objectMapper.readTree(corpoContrib).get("id").asText();

        mockMvc.perform(post("/api/iss/contribuintes/" + contribuinteId + "/credenciamento")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"statusCredenciamentoId\": \"%s\"}".formatted(STATUS_EM_ANALISE_ID)))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/iss/contribuintes/" + contribuinteId + "/credenciamento")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"statusCredenciamentoId\": \"%s\"}".formatted(STATUS_APROVADO_ID)))
            .andExpect(status().isOk());

        return contribuinteId;
    }
}
