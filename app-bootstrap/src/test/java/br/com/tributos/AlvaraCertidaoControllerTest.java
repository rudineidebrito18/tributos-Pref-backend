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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Critério de aceite Sprint 5: emitir alvará, download PDF, emitir certidão e validar via endpoint público.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Testcontainers
class AlvaraCertidaoControllerTest {

    private static final String TENANT_SLUG = "demo";
    private static final String TIPO_ALVARA_ID = "70000001-0000-4000-8000-000000000001";
    private static final String TIPO_CONTRIBUINTE_ID = "b0000001-0000-4000-8000-000000000001";
    private static final String SITUACAO_ATIVA_ID = "c0000001-0000-4000-8000-000000000001";
    private static final String REGIME_SIMPLES_ID = "d0000001-0000-4000-8000-000000000001";
    private static final String STATUS_EM_ANALISE_ID = "a0000001-0000-4000-8000-000000000002";
    private static final String STATUS_APROVADO_ID = "a0000001-0000-4000-8000-000000000003";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveEmitirAlvaraDownloadPdfEmitirCertidaoEValidarPublicamente() throws Exception {
        String token = login();
        String pessoaContribuinteId = cadastrarPessoaJuridica(token, "22.333.444/0001-55", "Empresa Alvará Teste");
        String contribuinteId = cadastrarEAprovarCredenciamento(token, pessoaContribuinteId);

        String corpoAlvara = mockMvc.perform(post("/api/iss/alvaras/emitir")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "contribuinteId": "%s",
                      "tipoAlvaraId": "%s",
                      "dataExpedicao": "2024-01-15",
                      "situacaoFiscal": "REGULAR"
                    }
                    """.formatted(contribuinteId, TIPO_ALVARA_ID)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.numero").value(1))
            .andExpect(jsonPath("$.situacaoFiscal").value("REGULAR"))
            .andReturn().getResponse().getContentAsString();

        String alvaraId = objectMapper.readTree(corpoAlvara).get("id").asText();
        String codigoVerificacaoAlvara = objectMapper.readTree(corpoAlvara).get("codigoVerificacao").asText();

        mockMvc.perform(get("/api/iss/alvaras/%s/pdf".formatted(alvaraId))
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        mockMvc.perform(get("/api/iss/alvaras")
                .header("Authorization", "Bearer " + token)
                .param("contribuinteId", contribuinteId)
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)));

        String corpoCertidao = mockMvc.perform(post("/api/iss/certidoes/emitir")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "contribuinteId": "%s",
                      "tipo": "NADA_CONSTA"
                    }
                    """.formatted(contribuinteId)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.numero").value(1))
            .andExpect(jsonPath("$.tipo").value("NADA_CONSTA"))
            .andReturn().getResponse().getContentAsString();

        String certidaoId = objectMapper.readTree(corpoCertidao).get("id").asText();
        String codigoVerificacaoCertidao = objectMapper.readTree(corpoCertidao).get("codigoVerificacao").asText();

        mockMvc.perform(get("/api/iss/certidoes/%s/pdf".formatted(certidaoId))
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        mockMvc.perform(get("/api/public/tenants/%s/iss/validar/%s".formatted(TENANT_SLUG, codigoVerificacaoAlvara)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tipoDocumento").value("ALVARA"))
            .andExpect(jsonPath("$.codigoVerificacao").value(codigoVerificacaoAlvara))
            .andExpect(jsonPath("$.vigente").value(true));

        mockMvc.perform(get("/api/public/tenants/%s/iss/validar/%s".formatted(TENANT_SLUG, codigoVerificacaoCertidao)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tipoDocumento").value("CERTIDAO"))
            .andExpect(jsonPath("$.tipoCertidao").value("NADA_CONSTA"))
            .andExpect(jsonPath("$.codigoVerificacao").value(codigoVerificacaoCertidao))
            .andExpect(jsonPath("$.vigente").value(true));
    }

    private String cadastrarEAprovarCredenciamento(String token, String pessoaId) throws Exception {
        String corpoContribuinte = mockMvc.perform(post("/api/iss/contribuintes")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "pessoaId": "%s",
                      "inscricaoMunicipal": "789012",
                      "tipoContribuinteId": "%s",
                      "situacaoCadastralId": "%s",
                      "regimeTributarioId": "%s"
                    }
                    """.formatted(pessoaId, TIPO_CONTRIBUINTE_ID, SITUACAO_ATIVA_ID, REGIME_SIMPLES_ID)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        String contribuinteId = objectMapper.readTree(corpoContribuinte).get("id").asText();

        String corpoSolicitacao = mockMvc.perform(post("/api/iss/credenciamento/solicitar")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"contribuinteId\": \"%s\"}".formatted(contribuinteId)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.statusId").value(STATUS_EM_ANALISE_ID))
            .andReturn().getResponse().getContentAsString();

        String solicitacaoId = objectMapper.readTree(corpoSolicitacao).get("id").asText();

        mockMvc.perform(post("/api/iss/credenciamento/solicitacoes/%s/aprovar".formatted(solicitacaoId))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"observacao\": \"Documentação conferida.\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statusId").value(STATUS_APROVADO_ID));

        return contribuinteId;
    }

    private String cadastrarPessoaJuridica(String token, String cpfCnpj, String nome) throws Exception {
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
                      "cpfCnpj": "%s",
                      "nome": "%s",
                      "razaoSocial": "%s Ltda",
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
                    """.formatted(cpfCnpj, nome, nome, cidadeId)))
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
