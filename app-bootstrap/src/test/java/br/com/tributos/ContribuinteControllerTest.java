package br.com.tributos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.tributos.support.AbstractIntegrationTest;

import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ContribuinteControllerTest extends AbstractIntegrationTest {

    private static final String TENANT_SLUG = "demo";
    private static final String TIPO_CONTRIBUINTE_ID = "b0000001-0000-4000-8000-000000000001";
    private static final String SITUACAO_ATIVA_ID = "c0000001-0000-4000-8000-000000000001";
    private static final String REGIME_SIMPLES_ID = "d0000001-0000-4000-8000-000000000001";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveSalvarCamposAuditadosListarEGerarSenhaSemExporSenha() throws Exception {
        String token = login();
        String sufixo = String.valueOf(System.currentTimeMillis() % 100000);
        String pessoaId = cadastrarPessoaJuridica(token, sufixo);

        String corpoCriado = mockMvc.perform(post("/api/iss/contribuintes")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "pessoaId": "%s",
                      "inscricaoMunicipal": "%s",
                      "tipoContribuinteId": "%s",
                      "situacaoCadastralId": "%s",
                      "regimeTributarioId": "%s",
                      "nomeFantasia": "Empresa Auditada",
                      "inscricaoEstadual": "123456789",
                      "contato": "João Silva",
                      "telefone2": "67999998888",
                      "emailNota": "notas-%s@empresa-auditada.test"
                    }
                    """.formatted(pessoaId, sufixo, TIPO_CONTRIBUINTE_ID, SITUACAO_ATIVA_ID, REGIME_SIMPLES_ID, sufixo)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nomeFantasia").value("Empresa Auditada"))
            .andExpect(jsonPath("$.emailNota").value("notas-" + sufixo + "@empresa-auditada.test"))
            .andReturn().getResponse().getContentAsString();

        String contribuinteId = objectMapper.readTree(corpoCriado).get("id").asText();

        mockMvc.perform(get("/api/iss/contribuintes/" + contribuinteId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.inscricaoEstadual").value("123456789"));

        mockMvc.perform(get("/api/iss/contribuintes")
                .header("Authorization", "Bearer " + token)
                .param("page", "0")
                .param("size", "50"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[?(@.id=='" + contribuinteId + "')].cpfCnpj").exists())
            .andExpect(jsonPath("$.content[?(@.id=='" + contribuinteId + "')].email").value("notas-" + sufixo + "@empresa-auditada.test"));

        String respostaSenha = mockMvc.perform(post("/api/iss/contribuintes/%s/gerar-senha-acesso".formatted(contribuinteId))
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.loginCriado").value(sufixo))
            .andExpect(jsonPath("$.senhaEnviadaPara").value("notas-" + sufixo + "@empresa-auditada.test"))
            .andReturn().getResponse().getContentAsString();

        assertFalse(respostaSenha.toLowerCase().contains("password"));
        assertFalse(respostaSenha.contains("$2"));
    }

    private String cadastrarPessoaJuridica(String token, String sufixo) throws Exception {
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
                      "cpfCnpj": "11.444.777/0001-61",
                      "nome": "Empresa Campos Auditados LTDA",
                      "razaoSocial": "Empresa Campos Auditados Ltda",
                      "email": "notas-%s@empresa-auditada.test",
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
                    """.formatted(sufixo, cidadeId)))
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
