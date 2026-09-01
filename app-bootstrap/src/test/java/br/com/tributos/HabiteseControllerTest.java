package br.com.tributos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.tributos.support.AbstractIntegrationTest;

import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HabiteseControllerTest extends AbstractIntegrationTest {

    private static final String TENANT_SLUG = "demo";
    private static final String TIPO_PREDIAL_ID = "80000001-0000-4000-8000-000000000001";
    private static final String TIPO_EDIFICACAO_ID = "80000002-0000-4000-8000-000000000001";
    private static final String DESTINACAO_RESIDENCIAL_ID = "80000003-0000-4000-8000-000000000001";
    private static final String ZONA_CENTRO_ID = "80000006-0000-4000-8000-000000000001";
    private static final String TIPO_CONTRIBUINTE_ID = "b0000001-0000-4000-8000-000000000001";
    private static final String SITUACAO_ATIVA_ID = "c0000001-0000-4000-8000-000000000001";
    private static final String REGIME_SIMPLES_ID = "d0000001-0000-4000-8000-000000000001";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveEmitirHabiteseComResponsaveisValorCalculadoEGuiaFinanceira() throws Exception {
        String token = login();
        String sufixo = String.valueOf(System.currentTimeMillis() % 100000);
        String pessoaId = cadastrarPessoaFisica(token, gerarCpf(sufixo + "1"), "Proprietário Habite-se");
        String contribuinteId = cadastrarContribuinte(token, pessoaId, "IMHAB" + sufixo);
        String imovelId = cadastrarImovel(token, pessoaId);

        String tipoId = objectMapper.readTree(
            mockMvc.perform(post("/api/iptu/apoio/habitese-tipos")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "nome": "Habite-se E62 %s",
                          "titulo": "Habite-se E62",
                          "ativo": true,
                          "permiteDesconto": false,
                          "habilitaCalculoValor": false,
                          "valor": 500.00
                        }
                        """.formatted(sufixo)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString()
        ).get("id").asText();

        String corpoHabitese = mockMvc.perform(post("/api/iptu/imoveis/%s/habiteses".formatted(imovelId))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "tipoId": "%s",
                      "dataEmissao": "2024-03-15",
                      "ano": 2024,
                      "contribuinteId": "%s",
                      "areaImovel": 120.5,
                      "dataConclusao": "2024-03-01",
                      "valorBaseCalculo": 10.00,
                      "desconto": 0,
                      "responsaveis": [
                        {
                          "nome": "Eng. João Silva",
                          "profissao": "Engenheiro Civil",
                          "documento": "CREA 12345"
                        },
                        {
                          "nome": "Arq. Maria Souza",
                          "profissao": "Arquiteta",
                          "documento": "CAU 67890"
                        }
                      ]
                    }
                    """.formatted(tipoId, contribuinteId)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.valor").value(500))
            .andExpect(jsonPath("$.codigoVerificacao").value(org.hamcrest.Matchers.matchesPattern("[A-Z0-9]{20}")))
            .andExpect(jsonPath("$.situacaoFiscal").value("PENDENTE"))
            .andExpect(jsonPath("$.responsaveis", hasSize(2)))
            .andExpect(jsonPath("$.responsaveis[0].ordem").value(1))
            .andExpect(jsonPath("$.responsaveis[1].ordem").value(2))
            .andReturn().getResponse().getContentAsString();

        String habiteseId = objectMapper.readTree(corpoHabitese).get("id").asText();
        String codigoVerificacaoHabitese = objectMapper.readTree(corpoHabitese).get("codigoVerificacao").asText();

        mockMvc.perform(get("/api/public/tenants/%s/documentos/validar/%s".formatted(TENANT_SLUG, codigoVerificacaoHabitese)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tipoDocumento").value("HABITE_SE"))
            .andExpect(jsonPath("$.codigoVerificacao").value(codigoVerificacaoHabitese))
            .andExpect(jsonPath("$.vigente").value(true));

        mockMvc.perform(get("/api/iptu/imoveis/%s/habiteses".formatted(imovelId))
                .header("Authorization", "Bearer " + token)
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].responsaveis", hasSize(2)));

        mockMvc.perform(get("/api/financeiro/guias-arrecadacao")
                .header("Authorization", "Bearer " + token)
                .param("tipoTributo", "HABITE_SE")
                .param("situacao", "PENDENTE")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[?(@.origemId == '%s')]", habiteseId).value(hasSize(1)))
            .andExpect(jsonPath("$.content[?(@.origemId == '%s')].valor", habiteseId).value(hasItem(500.0)));
    }

    @Test
    void deveIgnorarValorInformadoPeloCliente() throws Exception {
        String token = login();
        String sufixo = String.valueOf(System.currentTimeMillis() % 100000);
        String pessoaId = cadastrarPessoaFisica(token, gerarCpf(sufixo + "2"), "Proprietário Habite-se Valor");
        String contribuinteId = cadastrarContribuinte(token, pessoaId, "IMHAB" + sufixo + "V");
        String imovelId = cadastrarImovel(token, pessoaId);

        String tipoId = objectMapper.readTree(
            mockMvc.perform(post("/api/iptu/apoio/habitese-tipos")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "nome": "Habite-se Valor Fixo %s",
                          "titulo": "Habite-se Valor Fixo",
                          "ativo": true,
                          "permiteDesconto": false,
                          "habilitaCalculoValor": false,
                          "valor": 500.00
                        }
                        """.formatted(sufixo)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString()
        ).get("id").asText();

        mockMvc.perform(post("/api/iptu/imoveis/%s/habiteses".formatted(imovelId))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "tipoId": "%s",
                      "dataEmissao": "2024-03-15",
                      "ano": 2024,
                      "contribuinteId": "%s",
                      "areaImovel": 120.5,
                      "dataConclusao": "2024-03-01",
                      "valorBaseCalculo": 10.00,
                      "desconto": 0,
                      "valor": 1
                    }
                    """.formatted(tipoId, contribuinteId)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.valor").value(500));
    }

    private String cadastrarImovel(String token, String pessoaId) throws Exception {
        String corpo = mockMvc.perform(post("/api/iptu/imoveis")
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
                    """.formatted(pessoaId, TIPO_PREDIAL_ID, TIPO_EDIFICACAO_ID, DESTINACAO_RESIDENCIAL_ID, ZONA_CENTRO_ID)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("id").asText();
    }

    private String cadastrarContribuinte(String token, String pessoaId, String inscricaoMunicipal) throws Exception {
        String corpo = mockMvc.perform(post("/api/iss/contribuintes")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "pessoaId": "%s",
                      "inscricaoMunicipal": "%s",
                      "tipoContribuinteId": "%s",
                      "situacaoCadastralId": "%s",
                      "regimeTributarioId": "%s"
                    }
                    """.formatted(pessoaId, inscricaoMunicipal, TIPO_CONTRIBUINTE_ID, SITUACAO_ATIVA_ID, REGIME_SIMPLES_ID)))
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

    private static String gerarCpf(String semente) {
        String base = String.format("%09d", Math.abs(semente.hashCode()) % 1_000_000_000L);
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(base.charAt(i)) * (10 - i);
        }
        int digito1 = 11 - (soma % 11);
        if (digito1 >= 10) {
            digito1 = 0;
        }
        soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(base.charAt(i)) * (11 - i);
        }
        soma += digito1 * 2;
        int digito2 = 11 - (soma % 11);
        if (digito2 >= 10) {
            digito2 = 0;
        }
        String digitos = base + digito1 + digito2;
        return "%s.%s.%s-%s".formatted(
            digitos.substring(0, 3),
            digitos.substring(3, 6),
            digitos.substring(6, 9),
            digitos.substring(9)
        );
    }
}
