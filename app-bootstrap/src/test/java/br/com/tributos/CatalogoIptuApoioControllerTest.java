package br.com.tributos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import br.com.tributos.support.AbstractIntegrationTest;

import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CatalogoIptuApoioControllerTest extends AbstractIntegrationTest {

    private static final String TENANT_SLUG = "demo";
    private static final String TIPO_PREDIAL_ID = "80000001-0000-4000-8000-000000000001";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveRejeitarAliquotaIptuAcimaDe100() throws Exception {
        String token = loginAdmin();

        mockMvc.perform(post("/api/iptu/apoio/destinacoes")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "nome": "Industrial",
                      "ativo": true,
                      "tipoImovelId": "%s",
                      "aliquotaIptu": 150
                    }
                    """.formatted(TIPO_PREDIAL_ID)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deveCriarHabiteseTipoComAssinaturaERecuperarPorDownload() throws Exception {
        String token = loginAdmin();

        String corpo = mockMvc.perform(post("/api/iptu/apoio/habitese-tipos")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "nome": "Provisório E34",
                      "ativo": true,
                      "titulo": "Habite-se Provisório",
                      "permiteDesconto": false,
                      "habilitaCalculoValor": true,
                      "valor": 250.00,
                      "secretaria": "Secretaria de Obras",
                      "cargo": "Secretário"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.titulo").value("Habite-se Provisório"))
            .andReturn().getResponse().getContentAsString();

        String tipoId = objectMapper.readTree(corpo).get("id").asText();

        MockMultipartFile arquivo = new MockMultipartFile(
            "arquivo",
            "assinatura.png",
            "image/png",
            new byte[] {(byte) 0x89, 0x50, 0x4E, 0x47}
        );

        mockMvc.perform(multipart("/api/iptu/apoio/habitese-tipos/{id}/assinatura", tipoId)
                .file(arquivo)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.assinaturaDocumentoId").exists());

        mockMvc.perform(get("/api/iptu/apoio/habitese-tipos/{id}/assinatura/download", tipoId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Disposition", "attachment; filename=\"assinatura.png\""));
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
