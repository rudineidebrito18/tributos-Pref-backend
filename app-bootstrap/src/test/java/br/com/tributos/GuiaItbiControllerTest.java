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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Critério de aceite Sprint 9: solicitar guia ITBI, pagar e confirmar transferência no imóvel.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Testcontainers
class GuiaItbiControllerTest {

    private static final String TENANT_SLUG = "demo";
    private static final String TIPO_PREDIAL_ID = "80000001-0000-4000-8000-000000000001";
    private static final String TIPO_EDIFICACAO_ID = "80000002-0000-4000-8000-000000000001";
    private static final String TIPO_ITBI_ID = "a1000001-0000-4000-8000-000000000001";
    private static final String NATUREZA_ID = "a1000002-0000-4000-8000-000000000001";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveSolicitarPagarETransferirTitularidade() throws Exception {
        String token = login();
        String vendedorId = cadastrarPessoa(token, "FISICA", "111.444.777-35", "Vendedor ITBI");
        String compradorId = cadastrarPessoa(token, "FISICA", "390.533.447-05", "Comprador ITBI");

        String imovelId = cadastrarImovel(token, vendedorId);

        String corpoGuia = mockMvc.perform(post("/api/itbi/guias")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "imovelId": "%s",
                      "adquirenteId": "%s",
                      "tipoGuiaId": "%s",
                      "naturezaTransmissaoId": "%s",
                      "valorTransacao": 250000
                    }
                    """.formatted(imovelId, compradorId, TIPO_ITBI_ID, NATUREZA_ID)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.valorItbi").exists())
            .andReturn().getResponse().getContentAsString();

        String guiaItbiId = objectMapper.readTree(corpoGuia).get("id").asText();

        String corpoFinanceiro = mockMvc.perform(get("/api/financeiro/guias-arrecadacao")
                .header("Authorization", "Bearer " + token)
                .param("tipoTributo", "ITBI"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        String guiaFinanceiroId = objectMapper.readTree(corpoFinanceiro).get("content").get(0).get("id").asText();

        mockMvc.perform(post("/api/financeiro/guias-arrecadacao/" + guiaFinanceiroId + "/simular-pix")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/financeiro/guias-arrecadacao/" + guiaFinanceiroId + "/confirmar-pix")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.situacao").value("PAGA"));

        mockMvc.perform(post("/api/itbi/guias/" + guiaItbiId + "/confirmar-transferencia")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.transferenciaTitularidadeRealizada").value(true));

        mockMvc.perform(get("/api/iptu/imoveis/" + imovelId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.proprietarioId").value(compradorId));
    }

    private String login() throws Exception {
        String corpo = mockMvc.perform(post("/api/auth/login")
                .header("X-Tenant-Slug", TENANT_SLUG)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"usuario\":\"admin\",\"senha\":\"Demo@123\"}"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("tokens").get("accessToken").asText();
    }

    private String cadastrarPessoa(String token, String tipo, String cpfCnpj, String nome) throws Exception {
        String corpo = mockMvc.perform(post("/api/cadastro/pessoas")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipo\":\"%s\",\"nome\":\"%s\",\"cpfCnpj\":\"%s\"}".formatted(tipo, nome, cpfCnpj)))
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
                      "areaTerreno": 300,
                      "areaConstruida": 120,
                      "valorVenalTerreno": 100000,
                      "valorVenalConstrucao": 150000,
                      "situacao": "ATIVO"
                    }
                    """.formatted(proprietarioId, TIPO_PREDIAL_ID, TIPO_EDIFICACAO_ID)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("id").asText();
    }
}
