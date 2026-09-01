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

class CertidaoNegativaImovelControllerTest extends AbstractIntegrationTest {

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
    void deveRecusarEmissaoQuandoImovelPossuiIptuPendente() throws Exception {
        String token = login();
        String sufixo = String.valueOf(System.currentTimeMillis() % 100000);
        String pessoaId = cadastrarPessoaFisica(token, gerarCpf(sufixo + "3"), "Proprietário CND IPTU");
        String imovelId = cadastrarImovel(token, pessoaId);
        String situacaoCndId = cadastrarSituacaoCnd(token);

        mockMvc.perform(post("/api/financeiro/guias-arrecadacao/avulso")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "contribuinteId": "%s",
                      "valor": 150.00,
                      "dataVencimento": "2026-12-31",
                      "descricao": "IPTU pendente teste",
                      "tipoTributo": "IPTU",
                      "tipoTributacao": "TRIBUTAVEL"
                    }
                    """.formatted(pessoaId)))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/iptu/imoveis/%s/certidoes-negativas".formatted(imovelId))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "situacaoCndId": "%s",
                      "observacao": "Tentativa com IPTU pendente"
                    }
                    """.formatted(situacaoCndId)))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveEmitirCertidaoComSituacaoCndEObservacao() throws Exception {
        String token = login();
        String sufixo = String.valueOf(System.currentTimeMillis() % 100000);
        String pessoaId = cadastrarPessoaFisica(token, gerarCpf(sufixo + "4"), "Proprietário CND OK");
        String imovelId = cadastrarImovel(token, pessoaId);
        String situacaoCndId = cadastrarSituacaoCnd(token);

        mockMvc.perform(post("/api/iptu/imoveis/%s/certidoes-negativas".formatted(imovelId))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "validade": "2026-12-31",
                      "situacaoCndId": "%s",
                      "observacao": "Certidão emitida sem pendências"
                    }
                    """.formatted(situacaoCndId)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.situacaoCndId").value(situacaoCndId))
            .andExpect(jsonPath("$.observacao").value("Certidão emitida sem pendências"))
            .andExpect(jsonPath("$.codigoVerificacao").isNotEmpty());
    }

    private String cadastrarSituacaoCnd(String token) throws Exception {
        String sufixo = String.valueOf(System.currentTimeMillis() % 100000);
        String corpo = mockMvc.perform(post("/api/iss/situacoes-cnd")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "descricao": "Nada consta imóvel %s",
                      "titulo": "Negativa %s",
                      "ativo": true
                    }
                    """.formatted(sufixo, sufixo)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("id").asText();
    }

    private String cadastrarImovel(String token, String pessoaId) throws Exception {
        cadastrarContribuinte(token, pessoaId, "IMCND" + (System.nanoTime() % 100000000));
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
                    """.formatted(
                    pessoaId,
                    TIPO_PREDIAL_ID,
                    TIPO_EDIFICACAO_ID,
                    DESTINACAO_RESIDENCIAL_ID,
                    ZONA_CENTRO_ID
                )))
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
