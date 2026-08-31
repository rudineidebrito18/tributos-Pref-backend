package br.com.tributos;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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

/**
 * Critério de aceite Sprint 7: parametrização IPTU e geração de lançamento anual.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LancamentoIptuControllerTest extends AbstractIntegrationTest {

    private static final String TENANT_SLUG = "demo";
    private static final String TIPO_PREDIAL_ID = "80000001-0000-4000-8000-000000000001";
    private static final String TIPO_EDIFICACAO_ID = "80000002-0000-4000-8000-000000000001";
    private static final String DESTINACAO_RESIDENCIAL_ID = "80000003-0000-4000-8000-000000000001";
    private static final String ZONA_CENTRO_ID = "80000006-0000-4000-8000-000000000001";
    private static final int EXERCICIO = 2025;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    void deveGerarLancamentoAnualComParcelas() throws Exception {
        String token = login();
        String pessoaId = cadastrarPessoaFisica(token, "100.000.005-23", "Maria Proprietária IPTU Lancamento");

        String corpoImovel = mockMvc.perform(post("/api/iptu/imoveis")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "proprietarioId": "%s",
                      "tipoId": "%s",
                      "areaConstruida": 120.5,
                      "areaTerreno": 200,
                      "tipoEdificacaoId": "%s",
                      "destinacaoId": "%s",
                      "zonaFiscalId": "%s",
                      "valorVenalTerreno": 150000,
                      "valorVenalConstrucao": 280000
                    }
                    """.formatted(
                    pessoaId,
                    TIPO_PREDIAL_ID,
                    TIPO_EDIFICACAO_ID,
                    DESTINACAO_RESIDENCIAL_ID,
                    ZONA_CENTRO_ID
                )))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        String imovelId = objectMapper.readTree(corpoImovel).get("id").asText();

        mockMvc.perform(get("/api/iptu/exercicios/%d/parametrizacao/status".formatted(EXERCICIO))
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.completo").value(true))
            .andExpect(jsonPath("$.zonasOk").value(true))
            .andExpect(jsonPath("$.valoresTerrenoOk").value(true))
            .andExpect(jsonPath("$.aliquotasOk").value(true))
            .andExpect(jsonPath("$.imoveisSemZona").value(0));

        mockMvc.perform(post("/api/iptu/exercicios/%d/gerar-lancamentos".formatted(EXERCICIO))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"numeroParcelas\": 10}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$", hasSize(1)));

        String corpoLista = mockMvc.perform(get("/api/iptu/lancamentos")
                .header("Authorization", "Bearer " + token)
                .param("exercicio", String.valueOf(EXERCICIO))
                .param("imovelId", imovelId)
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andReturn().getResponse().getContentAsString();

        String lancamentoId = objectMapper.readTree(corpoLista).get("content").get(0).get("id").asText();

        mockMvc.perform(get("/api/iptu/lancamentos/" + lancamentoId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.parcelas", hasSize(10)))
            .andExpect(jsonPath("$.numeroParcelas").value(10));
    }

    @Test
    @Order(2)
    void naoDeveGerarLancamentoComParametrizacaoIncompleta() throws Exception {
        String token = login();

        mockMvc.perform(post("/api/iptu/zonas-fiscais")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "nome": "ZONA SEM ALIQUOTA",
                      "fatorValorizacao": 1.0,
                      "ativo": true
                    }
                    """))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/api/iptu/exercicios/%d/parametrizacao/status".formatted(EXERCICIO))
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.completo").value(false))
            .andExpect(jsonPath("$.aliquotasOk").value(false))
            .andExpect(jsonPath("$.combinacoesFaltantes").isNotEmpty());

        mockMvc.perform(post("/api/iptu/exercicios/%d/gerar-lancamentos".formatted(EXERCICIO))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isUnprocessableEntity());
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
