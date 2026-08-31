package br.com.tributos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.tributos.support.AbstractIntegrationTest;

import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PortalContribuinteControllerTest extends AbstractIntegrationTest {

    private static final String TENANT_SLUG = "demo";
    private static final String TIPO_PREDIAL_ID = "80000001-0000-4000-8000-000000000001";
    private static final String TIPO_EDIFICACAO_ID = "80000002-0000-4000-8000-000000000001";
    private static final String TIPO_ITBI_ID = "a1000001-0000-4000-8000-000000000001";
    private static final String NATUREZA_ID = "a1000002-0000-4000-8000-000000000001";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveConsultarSituacaoFiscalESegundaViaPublica() throws Exception {
        String token = login();
        String vendedor = cadastrarPessoa(token, "100.000.007-95", "Portal Vendedor");
        String comprador = cadastrarPessoa(token, "390.533.447-05", "Portal Comprador");
        String imovelId = cadastrarImovel(token, vendedor);

        mockMvc.perform(post("/api/itbi/guias")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "imovelId": "%s",
                      "adquirenteId": "%s",
                      "tipoGuiaId": "%s",
                      "naturezaTransmissaoId": "%s",
                      "valorTransacao": 200000
                    }
                    """.formatted(imovelId, comprador, TIPO_ITBI_ID, NATUREZA_ID)))
            .andExpect(status().isCreated());

        String corpoFinanceiro = mockMvc.perform(get("/api/financeiro/guias-arrecadacao")
                .header("Authorization", "Bearer " + token)
                .param("tipoTributo", "ITBI"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        long numeroGuiaFinanceiro = objectMapper.readTree(corpoFinanceiro).get("content").get(0).get("numero").asLong();

        mockMvc.perform(get("/api/public/tenants/%s/contribuinte/situacao-fiscal".formatted(TENANT_SLUG))
                .param("cpfCnpj", "39053344705"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.possuiPendencia").value(true))
            .andExpect(jsonPath("$.guiasPendentes").value(1));

        mockMvc.perform(get("/api/public/tenants/%s/financeiro/guias/%d/segunda-via".formatted(TENANT_SLUG, numeroGuiaFinanceiro))
                .param("cpfCnpj", "39053344705"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.numero").value(numeroGuiaFinanceiro))
            .andExpect(jsonPath("$.tipoTributo").value("ITBI"));
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

    private String cadastrarPessoa(String token, String cpfCnpj, String nome) throws Exception {
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

    private String cadastrarImovel(String token, String proprietarioId) throws Exception {
        String corpo = mockMvc.perform(post("/api/iptu/imoveis")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "proprietarioId": "%s",
                      "tipoId": "%s",
                      "tipoEdificacaoId": "%s",
                      "areaTerreno": 200,
                      "areaConstruida": 80,
                      "valorVenalTerreno": 80000,
                      "valorVenalConstrucao": 120000,
                      "situacao": "ATIVO"
                    }
                    """.formatted(proprietarioId, TIPO_PREDIAL_ID, TIPO_EDIFICACAO_ID)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("id").asText();
    }
}
