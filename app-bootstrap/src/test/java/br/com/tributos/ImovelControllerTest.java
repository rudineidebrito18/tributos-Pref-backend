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

/**
 * Critério de aceite Sprint 6: cadastrar imóveis, emitir habite-se e certidão negativa.
 */
class ImovelControllerTest extends AbstractIntegrationTest {

    private static final String TENANT_SLUG = "demo";
    private static final String TIPO_PREDIAL_ID = "80000001-0000-4000-8000-000000000001";
    private static final String TIPO_TERRITORIAL_ID = "80000001-0000-4000-8000-000000000002";
    private static final String TIPO_EDIFICACAO_ID = "80000002-0000-4000-8000-000000000001";
    private static final String DESTINACAO_RESIDENCIAL_ID = "80000003-0000-4000-8000-000000000001";
    private static final String HABITESE_TIPO_ID = "80000005-0000-4000-8000-000000000001";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCadastrarImoveisEmitirHabiteseECertidaoNegativa() throws Exception {
        String token = login();
        String pessoaPfId = cadastrarPessoaFisica(token, "100.000.004-42", "Maria Proprietária IPTU");
        String pessoaPjId = cadastrarPessoaJuridica(token, "77.888.999/0001-81", "Empresa Proprietária IPTU");

        String corpoPredial = mockMvc.perform(post("/api/iptu/imoveis")
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
                      "valorVenalTerreno": 150000,
                      "valorVenalConstrucao": 280000
                    }
                    """.formatted(pessoaPfId, TIPO_PREDIAL_ID, TIPO_EDIFICACAO_ID, DESTINACAO_RESIDENCIAL_ID)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.situacao").value("ATIVO"))
            .andReturn().getResponse().getContentAsString();

        int numeroPredial = objectMapper.readTree(corpoPredial).get("numeroCadastro").asInt();
        String imovelPredialId = objectMapper.readTree(corpoPredial).get("id").asText();

        mockMvc.perform(post("/api/iptu/imoveis")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "proprietarioId": "%s",
                      "tipoId": "%s",
                      "areaTerreno": 500
                    }
                    """.formatted(pessoaPjId, TIPO_TERRITORIAL_ID)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.numeroCadastro").value(numeroPredial + 1));

        mockMvc.perform(post("/api/iptu/imoveis/%s/habiteses".formatted(imovelPredialId))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "tipoId": "%s",
                      "dataEmissao": "2024-03-15"
                    }
                    """.formatted(HABITESE_TIPO_ID)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.numero").isNumber());

        mockMvc.perform(get("/api/iptu/imoveis/%s/habiteses".formatted(imovelPredialId))
                .header("Authorization", "Bearer " + token)
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)));

        mockMvc.perform(post("/api/iptu/imoveis/%s/certidoes-negativas".formatted(imovelPredialId))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.numero").isNumber())
            .andExpect(jsonPath("$.codigoVerificacao").isNotEmpty());

        mockMvc.perform(get("/api/iptu/imoveis/%s/certidoes-negativas".formatted(imovelPredialId))
                .header("Authorization", "Bearer " + token)
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)));
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
