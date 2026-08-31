package br.com.tributos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import br.com.tributos.support.AbstractIntegrationTest;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PixWebhookControllerTest extends AbstractIntegrationTest {

    private static final String TENANT_SLUG = "demo";
    private static final String WEBHOOK_TOKEN = "token-webhook-teste";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void deveRejeitarWebhookSemCredencial() throws Exception {
        mockMvc.perform(post("/api/webhooks/pix/" + TENANT_SLUG)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpoWebhook("txid-qualquer", "E2E-1", "10.00")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRejeitarWebhookComTokenErrado() throws Exception {
        String token = login();
        salvarConfigPix(token);

        mockMvc.perform(post("/api/webhooks/pix/" + TENANT_SLUG)
                .header("X-Webhook-Token", "token-errado")
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpoWebhook("txid-qualquer", "E2E-1", "10.00")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void deveAceitarTxidOrfaoCom200() throws Exception {
        String token = login();
        salvarConfigPix(token);

        mockMvc.perform(post("/api/webhooks/pix/" + TENANT_SLUG)
                .header("X-Webhook-Token", WEBHOOK_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpoWebhook("txid-inexistente-999", "E2E-ORFAO", "10.00")))
            .andExpect(status().isOk());

        Integer logs = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM pix_conciliacao_log WHERE txid = ? AND status_novo = 'ORFAO'",
            Integer.class,
            "txid-inexistente-999"
        );
        org.assertj.core.api.Assertions.assertThat(logs).isEqualTo(1);
    }

    @Test
    void deveBaixarGuiaViaWebhookComTokenValido() throws Exception {
        String token = login();
        String guiaId = criarGuiaComPix(token, "11.222.333/0001-81", "Empresa Webhook Baixa");
        salvarConfigPix(token);

        String corpoGuia = mockMvc.perform(get("/api/financeiro/guias-arrecadacao/" + guiaId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        JsonNode guiaJson = objectMapper.readTree(corpoGuia);
        String pixTxid = guiaJson.get("pixTxid").asText();
        String valorGuia = guiaJson.get("valor").asText();

        mockMvc.perform(post("/api/webhooks/pix/" + TENANT_SLUG)
                .header("X-Webhook-Token", WEBHOOK_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpoWebhook(pixTxid, "E60746948202103082223A7540Db1234", valorGuia)))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/financeiro/guias-arrecadacao/" + guiaId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.situacao").value("PAGA"))
            .andExpect(jsonPath("$.statusPix").value("CONCLUIDA"));

        mockMvc.perform(post("/api/webhooks/pix/" + TENANT_SLUG)
                .header("X-Webhook-Token", WEBHOOK_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpoWebhook(pixTxid, "E60746948202103082223A7540Db1234", valorGuia)))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/financeiro/guias-arrecadacao/" + guiaId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.situacao").value("PAGA"));
    }

    @Test
    void deveManterGuiaPendenteComValorParcial() throws Exception {
        String token = login();
        String guiaId = criarGuiaComPix(token, "00.394.460/0058-87", "Empresa Webhook Parcial");
        salvarConfigPix(token);

        String corpoGuia = mockMvc.perform(get("/api/financeiro/guias-arrecadacao/" + guiaId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        JsonNode guiaJson = objectMapper.readTree(corpoGuia);
        String pixTxid = guiaJson.get("pixTxid").asText();
        java.math.BigDecimal valorGuia = guiaJson.get("valor").decimalValue();
        String valorParcial = valorGuia.divide(java.math.BigDecimal.valueOf(2)).toPlainString();

        mockMvc.perform(post("/api/webhooks/pix/" + TENANT_SLUG)
                .header("X-Webhook-Token", WEBHOOK_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpoWebhook(pixTxid, "E2E-PARCIAL", valorParcial)))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/financeiro/guias-arrecadacao/" + guiaId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.situacao").value("PENDENTE"))
            .andExpect(jsonPath("$.valorPago").value(valorGuia.divide(java.math.BigDecimal.valueOf(2)).doubleValue()));
    }

    private String criarGuiaComPix(String token, String cnpj, String nomeEmpresa) throws Exception {
        String pessoaId = cadastrarPessoa(token, cnpj, nomeEmpresa);
        String contribuinteId = credenciamento(token, pessoaId);
        emitirNota(token, contribuinteId, pessoaId);

        String listagem = mockMvc.perform(get("/api/financeiro/guias-arrecadacao")
                .header("Authorization", "Bearer " + token)
                .param("tipoTributo", "ISS")
                .param("contribuinteId", pessoaId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andReturn().getResponse().getContentAsString();

        String guiaId = objectMapper.readTree(listagem).get("content").get(0).get("id").asText();
        salvarConfigPix(token);

        mockMvc.perform(post("/api/financeiro/guias-arrecadacao/" + guiaId + "/pix")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());

        return guiaId;
    }

    private void emitirNota(String token, String contribuinteId, String pessoaTomadorId) throws Exception {
        String tomadorId = objectMapper.readTree(mockMvc.perform(post("/api/iss/tomadores")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"pessoaId\": \"%s\"}".formatted(pessoaTomadorId)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(post("/api/iss/notas-fiscais/emitir")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "contribuinteId": "%s",
                      "tomadorId": "%s",
                      "servicoId": "e0000002-0000-4000-8000-000000000002",
                      "competencia": "2024-06-01",
                      "valorServico": 10000,
                      "valorDeducoes": 0,
                      "receitaBrutaAcumulada12Meses": 200000
                    }
                    """.formatted(contribuinteId, tomadorId)))
            .andExpect(status().isCreated());
    }

    private String credenciamento(String token, String pessoaId) throws Exception {
        String contribuinteId = objectMapper.readTree(mockMvc.perform(post("/api/iss/contribuintes")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "pessoaId": "%s",
                      "inscricaoMunicipal": "WH-%s",
                      "tipoContribuinteId": "b0000001-0000-4000-8000-000000000001",
                      "situacaoCadastralId": "c0000001-0000-4000-8000-000000000001",
                      "regimeTributarioId": "d0000001-0000-4000-8000-000000000001"
                    }
                    """.formatted(pessoaId, pessoaId.substring(0, 8))))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString()).get("id").asText();

        String solicitacaoId = objectMapper.readTree(mockMvc.perform(post("/api/iss/credenciamento/solicitar")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"contribuinteId\": \"%s\"}".formatted(contribuinteId)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(post("/api/iss/credenciamento/solicitacoes/" + solicitacaoId + "/aprovar")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"observacao\": \"ok\"}"))
            .andExpect(status().isOk());

        return contribuinteId;
    }

    private String cadastrarPessoa(String token, String cnpj, String nome) throws Exception {
        String cidadeId = objectMapper.readTree(mockMvc.perform(get("/api/cadastro/territorio/cidades?uf=SP")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString()).get(0).get("id").asText();

        return objectMapper.readTree(mockMvc.perform(post("/api/cadastro/pessoas")
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
                        "logradouro": "Rua Teste",
                        "numero": "100",
                        "bairro": "Centro",
                        "cidadeId": "%s",
                        "principal": true
                      }]
                    }
                    """.formatted(cnpj, nome, cidadeId)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString()).get("id").asText();
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
                      "indicadorCodigoBarras": "N",
                      "webhookToken": "%s"
                    }
                    """.formatted(WEBHOOK_TOKEN)))
            .andExpect(status().isOk());
    }

    private String login() throws Exception {
        JsonNode resposta = objectMapper.readTree(mockMvc.perform(post("/api/auth/login")
                .header("X-Tenant-Slug", TENANT_SLUG)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"admin\",\"senha\":\"Demo@123\"}"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString());
        return resposta.get("tokens").get("accessToken").asText();
    }

    private static String corpoWebhook(String txid, String endToEndId, String valor) {
        return """
            {
              "pix": [{
                "endToEndId": "%s",
                "txid": "%s",
                "valor": "%s",
                "componentesValor": { "original": { "valor": "%s" } },
                "horario": "2022-07-27T14:30:47.00-03:00"
              }]
            }
            """.formatted(endToEndId, txid, valor, valor);
    }
}
