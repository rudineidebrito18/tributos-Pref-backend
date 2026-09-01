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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Critério de aceite Sprint 9 / E6.6: solicitar guia ITBI com partes da transmissão,
 * pagar, confirmar transferência e validar cálculo conforme auditoria.
 */
class GuiaItbiControllerTest extends AbstractIntegrationTest {

    private static final String TENANT_SLUG = "demo";
    private static final String TIPO_PREDIAL_ID = "80000001-0000-4000-8000-000000000001";
    private static final String TIPO_EDIFICACAO_ID = "80000002-0000-4000-8000-000000000001";
    private static final String ZONA_CENTRO_ID = "80000006-0000-4000-8000-000000000001";
    private static final String TIPO_ITBI_ID = "a1000001-0000-4000-8000-000000000001";
    private static final String NATUREZA_ID = "a1000002-0000-4000-8000-000000000001";
    private static final String TIPO_CONTRIBUINTE_ID = "b0000001-0000-4000-8000-000000000001";
    private static final String SITUACAO_ATIVA_ID = "c0000001-0000-4000-8000-000000000001";
    private static final String REGIME_SIMPLES_ID = "d0000001-0000-4000-8000-000000000001";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCalcularValorItbiConformeAuditoria() throws Exception {
        String token = login();
        String sufixo = String.valueOf(System.currentTimeMillis() % 100000);
        String vendedorPessoaId = cadastrarPessoa(token, gerarCpf(sufixo + "audit1"), "Vendedor ITBI Audit");
        String compradorPessoaId = cadastrarPessoa(token, gerarCpf(sufixo + "audit2"), "Comprador ITBI Audit");
        cadastrarContribuinte(token, vendedorPessoaId, "IMAUDV" + sufixo);
        cadastrarContribuinte(token, compradorPessoaId, "IMAUD" + sufixo);

        String imovelId = cadastrarImovel(token, vendedorPessoaId, 50000, 50000);

        mockMvc.perform(post("/api/itbi/guias")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "imovelId": "%s",
                      "adquirenteId": "%s",
                      "tipoGuiaId": "%s",
                      "naturezaTransmissaoId": "%s",
                      "valorTransacao": 171153.57
                    }
                    """.formatted(imovelId, compradorPessoaId, TIPO_ITBI_ID, NATUREZA_ID)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.valorItbi").value(3423.07))
            .andExpect(jsonPath("$.baseCalculo").value(171153.57));
    }

    @Test
    void deveSolicitarPagarETransferirTitularidadeComPartes() throws Exception {
        String token = login();
        String sufixo = String.valueOf(System.currentTimeMillis() % 100000);
        String vendedorPessoaId = cadastrarPessoa(token, gerarCpf(sufixo + "1"), "Vendedor ITBI");
        String comprador1PessoaId = cadastrarPessoa(token, gerarCpf(sufixo + "2"), "Comprador 1 ITBI");
        String comprador2PessoaId = cadastrarPessoa(token, gerarCpf(sufixo + "3"), "Comprador 2 ITBI");
        String vendedorContribuinteId = cadastrarContribuinte(token, vendedorPessoaId, "IM" + sufixo + "1");
        String comprador1ContribuinteId = cadastrarContribuinte(token, comprador1PessoaId, "IM" + sufixo + "2");
        String comprador2ContribuinteId = cadastrarContribuinte(token, comprador2PessoaId, "IM" + sufixo + "3");

        String imovelId = cadastrarImovel(token, vendedorPessoaId);

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
                    """.formatted(imovelId, comprador1PessoaId, TIPO_ITBI_ID, NATUREZA_ID)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.valorItbi").exists())
            .andReturn().getResponse().getContentAsString();

        String guiaItbiId = objectMapper.readTree(corpoGuia).get("id").asText();

        mockMvc.perform(post("/api/itbi/guias/" + guiaItbiId + "/partes")
                .header("Authorization", "Bearer " + token)
                .param("papel", "TRANSMITENTE")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "contribuinteId": "%s",
                      "porcentagem": 100,
                      "principal": true
                    }
                    """.formatted(vendedorContribuinteId)))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/itbi/guias/" + guiaItbiId + "/partes")
                .header("Authorization", "Bearer " + token)
                .param("papel", "ADQUIRENTE")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "contribuinteId": "%s",
                      "porcentagem": 50,
                      "principal": true
                    }
                    """.formatted(comprador1ContribuinteId)))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/itbi/guias/" + guiaItbiId + "/partes")
                .header("Authorization", "Bearer " + token)
                .param("papel", "ADQUIRENTE")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "contribuinteId": "%s",
                      "porcentagem": 50,
                      "principal": false
                    }
                    """.formatted(comprador2ContribuinteId)))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/api/itbi/guias/" + guiaItbiId + "/partes")
                .header("Authorization", "Bearer " + token)
                .param("papel", "TRANSMITENTE"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/api/itbi/guias/" + guiaItbiId + "/partes")
                .header("Authorization", "Bearer " + token)
                .param("papel", "ADQUIRENTE"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));

        String corpoFinanceiro = mockMvc.perform(get("/api/financeiro/guias-arrecadacao")
                .header("Authorization", "Bearer " + token)
                .param("tipoTributo", "ITBI"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        String guiaFinanceiroId = objectMapper.readTree(corpoFinanceiro).get("content").get(0).get("id").asText();

        salvarConfigPix(token);

        mockMvc.perform(post("/api/financeiro/guias-arrecadacao/" + guiaFinanceiroId + "/pix")
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

        mockMvc.perform(get("/api/iptu/imoveis/" + imovelId + "/proprietarios")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].porcentagem").value(50))
            .andExpect(jsonPath("$[1].porcentagem").value(50));

        mockMvc.perform(get("/api/iptu/imoveis/" + imovelId + "/titularidade")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)));

        mockMvc.perform(get("/api/iptu/imoveis/" + imovelId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.proprietarioId").value(comprador1PessoaId));
    }

    private String login() throws Exception {
        return login("admin", "Demo@123");
    }

    private String login(String login, String senha) throws Exception {
        String corpo = mockMvc.perform(post("/api/auth/login")
                .header("X-Tenant-Slug", TENANT_SLUG)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"%s\",\"senha\":\"%s\"}".formatted(login, senha)))
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

    private String cadastrarImovel(String token, String proprietarioId) throws Exception {
        return cadastrarImovel(token, proprietarioId, 100000, 150000);
    }

    private String cadastrarImovel(String token, String proprietarioId, long venalTerreno, long venalConstrucao) throws Exception {
        String corpo = mockMvc.perform(post("/api/iptu/imoveis")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "proprietarioId": "%s",
                      "tipoId": "%s",
                      "tipoEdificacaoId": "%s",
                      "zonaFiscalId": "%s",
                      "areaTerreno": 300,
                      "areaConstruida": 120,
                      "valorVenalTerreno": %d,
                      "valorVenalConstrucao": %d,
                      "situacao": "ATIVO"
                    }
                    """.formatted(proprietarioId, TIPO_PREDIAL_ID, TIPO_EDIFICACAO_ID, ZONA_CENTRO_ID, venalTerreno, venalConstrucao)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("id").asText();
    }

    private void salvarConfigPix(String token) throws Exception {
        mockMvc.perform(put("/api/plataforma/configuracao-pix/SANDBOX")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "ativo": true,
                      "clientId": "client-demo",
                      "clientSecret": "segredo123",
                      "developerApplicationKey": "dev-key-demo",
                      "escopos": "pix.arrecadacao-requisicao pix.arrecadacao-info",
                      "numeroConvenio": "123456",
                      "chavePix": "00000000000000000000000000000000000000000000",
                      "indicadorCodigoBarras": "N"
                    }
                    """))
            .andExpect(status().isOk());
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
