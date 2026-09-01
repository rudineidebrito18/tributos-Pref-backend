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

class RelatorioIssControllerTest extends AbstractIntegrationTest {

    private static final String TENANT_SLUG = "demo";
    private static final String SERVICO_ID = "e0000002-0000-4000-8000-000000000002";
    private static final String TIPO_CONTRIBUINTE_ID = "b0000001-0000-4000-8000-000000000001";
    private static final String SITUACAO_ATIVA_ID = "c0000001-0000-4000-8000-000000000001";
    private static final String REGIME_SIMPLES_ID = "d0000001-0000-4000-8000-000000000001";
    private static final String STATUS_APROVADO_ID = "a0000001-0000-4000-8000-000000000003";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveListarRelatorioIrpfPaginado() throws Exception {
        String token = login();

        mockMvc.perform(get("/api/iss/relatorios/irpf")
                .header("Authorization", "Bearer " + token)
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.conteudo.content").isArray());
    }

    @Test
    void deveListarRelatorioNotasPorTomador() throws Exception {
        String token = login();
        String sufixo = String.valueOf(System.nanoTime() % 100000);
        String pessoaContribuinteId = cadastrarPessoaJuridica(token, gerarCnpj(sufixo + "RI"), "Empresa Relatório ISS");
        String contribuinteId = cadastrarEAprovarCredenciamento(token, pessoaContribuinteId);
        String pessoaTomadorId = cadastrarPessoaFisica(token, gerarCpf(sufixo + "RT"), "Tomador Relatório");

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
                      "receitaBrutaAcumulada12Meses": 150000
                    }
                    """.formatted(contribuinteId, tomadorId, SERVICO_ID)))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/api/iss/relatorios/notas-tomador")
                .header("Authorization", "Bearer " + token)
                .param("tomadorId", tomadorId)
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
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

    private String cadastrarPessoaJuridica(String token, String cnpj, String razaoSocial) throws Exception {
        String cidadeId = buscarCidadeId(token);
        String corpo = mockMvc.perform(post("/api/cadastro/pessoas")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "tipoPessoa": "PJ",
                      "cpfCnpj": "%s",
                      "nome": "%s",
                      "ativo": true,
                      "enderecos": [{
                        "cep": "01310100",
                        "logradouro": "Rua Augusta",
                        "numero": "100",
                        "bairro": "Consolação",
                        "cidadeId": "%s",
                        "principal": true
                      }]
                    }
                    """.formatted(cnpj, razaoSocial, cidadeId)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("id").asText();
    }

    private String cadastrarPessoaFisica(String token, String cpf, String nome) throws Exception {
        String cidadeId = buscarCidadeId(token);
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
                        "numero": "200",
                        "bairro": "Consolação",
                        "cidadeId": "%s",
                        "principal": true
                      }]
                    }
                    """.formatted(cpf, nome, cidadeId)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("id").asText();
    }

    private String buscarCidadeId(String token) throws Exception {
        return objectMapper.readTree(
            mockMvc.perform(get("/api/cadastro/territorio/cidades?uf=SP")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()
        ).get(0).get("id").asText();
    }

    private String cadastrarEAprovarCredenciamento(String token, String pessoaId) throws Exception {
        String corpo = mockMvc.perform(post("/api/iss/contribuintes")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "pessoaId": "%s",
                      "inscricaoMunicipal": "IMREL%s",
                      "tipoContribuinteId": "%s",
                      "situacaoCadastralId": "%s",
                      "regimeTributarioId": "%s"
                    }
                    """.formatted(pessoaId, System.nanoTime() % 100000, TIPO_CONTRIBUINTE_ID, SITUACAO_ATIVA_ID, REGIME_SIMPLES_ID)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        String contribuinteId = objectMapper.readTree(corpo).get("id").asText();

        String corpoSolicitacao = mockMvc.perform(post("/api/iss/credenciamento/solicitar")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"contribuinteId\": \"%s\"}".formatted(contribuinteId)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        String solicitacaoId = objectMapper.readTree(corpoSolicitacao).get("id").asText();

        mockMvc.perform(post("/api/iss/credenciamento/solicitacoes/%s/aprovar".formatted(solicitacaoId))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"observacao\": \"Aprovado para teste.\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statusId").value(STATUS_APROVADO_ID));

        return contribuinteId;
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

    private static String gerarCnpj(String semente) {
        String base = String.format("%08d", Math.abs(semente.hashCode()) % 100_000_000L) + "0001";
        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int soma = 0;
        for (int i = 0; i < 12; i++) {
            soma += Character.getNumericValue(base.charAt(i)) * pesos1[i];
        }
        int digito1 = soma % 11 < 2 ? 0 : 11 - (soma % 11);
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        String comDigito1 = base + digito1;
        soma = 0;
        for (int i = 0; i < 13; i++) {
            soma += Character.getNumericValue(comDigito1.charAt(i)) * pesos2[i];
        }
        int digito2 = soma % 11 < 2 ? 0 : 11 - (soma % 11);
        String digitos = base + digito1 + digito2;
        return "%s.%s.%s/%s-%s".formatted(
            digitos.substring(0, 2),
            digitos.substring(2, 5),
            digitos.substring(5, 8),
            digitos.substring(8, 12),
            digitos.substring(12)
        );
    }
}
