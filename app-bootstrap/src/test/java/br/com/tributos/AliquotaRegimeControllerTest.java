package br.com.tributos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.tributos.support.AbstractIntegrationTest;

import static org.hamcrest.Matchers.closeTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Critério de aceite Sprint 3: calcular alíquota efetiva de ISS para Simples Nacional (Anexo III).
 */
class AliquotaRegimeControllerTest extends AbstractIntegrationTest {

    private static final String TENANT_SLUG = "demo";
    private static final String REGIME_SIMPLES_ID = "d0000001-0000-4000-8000-000000000001";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveCalcularAliquotaEfetivaParaSimplesNacional() throws Exception {
        String token = login();

        mockMvc.perform(post("/api/iss/regimes/%s/aliquotas/calcular".formatted(REGIME_SIMPLES_ID))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "receitaBrutaAcumulada12Meses": 200000,
                      "competencia": "2024-06-01"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.anexoSimples").value("III"))
            .andExpect(jsonPath("$.aliquotaNominal").value(11.2))
            .andExpect(jsonPath("$.aliquotaIssEfetiva").value(closeTo(2.1842, 0.0001)));
    }

    private String login() throws Exception {
        String corpo = mockMvc.perform(post("/api/auth/login")
                .header("X-Tenant-Slug", TENANT_SLUG)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"admin\",\"senha\":\"Demo@123\"}"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        return new tools.jackson.databind.ObjectMapper()
            .readTree(corpo)
            .get("tokens")
            .get("accessToken")
            .asText();
    }
}
