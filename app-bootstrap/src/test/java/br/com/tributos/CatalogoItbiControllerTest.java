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

class CatalogoItbiControllerTest extends AbstractIntegrationTest {

    private static final String TENANT_SLUG = "demo";
    private static final String TIPO_CALCULO_PERCENTUAL_ID = "a1000003-0000-4000-8000-000000000001";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCadastrarTipoGuiaComTipoCalculoIdEValorParcela() throws Exception {
        String token = loginAdmin();

        mockMvc.perform(get("/api/itbi/catalogo/tipos-calculo")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.descricao == 'PERCENTUAL_SOBRE_BASE')]").exists())
            .andExpect(jsonPath("$[?(@.descricao == 'VALOR_FIXO')]").exists());

        mockMvc.perform(post("/api/itbi/catalogo/tipos-guia")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "nome": "USUCAPIAO",
                      "aliquota": 0.02,
                      "ativo": true,
                      "tipoCalculoId": "%s",
                      "permiteDesconto": false,
                      "habilitaCalculoValor": true,
                      "valor": 500.00,
                      "valorParcela": 125.00,
                      "secretaria": "Secretaria da Fazenda",
                      "cargo": "Secretário"
                    }
                    """.formatted(TIPO_CALCULO_PERCENTUAL_ID)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.tipoCalculoId").value(TIPO_CALCULO_PERCENTUAL_ID))
            .andExpect(jsonPath("$.valorParcela").value(125.00))
            .andExpect(jsonPath("$.valor").value(500.00));
    }

    private String loginAdmin() throws Exception {
        String corpo = mockMvc.perform(post("/api/auth/login")
                .header("X-Tenant-Slug", TENANT_SLUG)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"admin\",\"senha\":\"Demo@123\"}"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("tokens").get("accessToken").asText();
    }
}
