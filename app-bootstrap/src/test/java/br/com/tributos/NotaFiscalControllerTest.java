package br.com.tributos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import br.com.tributos.support.AbstractIntegrationTest;

import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Critério de aceite Sprint 4: emitir nota fiscal, listar e cancelar.
 */
class NotaFiscalControllerTest extends AbstractIntegrationTest {

    private static final String TENANT_SLUG = "demo";
    private static final String TIPO_CONTRIBUINTE_ID = "b0000001-0000-4000-8000-000000000001";
    private static final String SITUACAO_ATIVA_ID = "c0000001-0000-4000-8000-000000000001";
    private static final String REGIME_SIMPLES_ID = "d0000001-0000-4000-8000-000000000001";
    private static final String STATUS_EM_ANALISE_ID = "a0000001-0000-4000-8000-000000000002";
    private static final String STATUS_APROVADO_ID = "a0000001-0000-4000-8000-000000000003";
    private static final String SERVICO_ID = "e0000002-0000-4000-8000-000000000002";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void deveEmitirNotaComAliquotaDoCatalogoAtividadeServico() throws Exception {
        String token = login();
        String pessoaContribuinteId = cadastrarPessoaJuridica(token, "00.000.000/0001-91", "Empresa Catalogo NFS-e");
        String contribuinteId = cadastrarEAprovarCredenciamento(token, pessoaContribuinteId, "765432");
        String pessoaTomadorId = cadastrarPessoaFisica(token, "100.000.008-76", "Maria Tomador Catalogo");

        String corpoTomador = mockMvc.perform(post("/api/iss/tomadores")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"pessoaId\": \"%s\"}".formatted(pessoaTomadorId)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        String tomadorId = objectMapper.readTree(corpoTomador).get("id").asText();

        String localIncidenciaId = objectMapper.readTree(
            mockMvc.perform(get("/api/iss/locais-incidencia")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()
        ).get(0).get("id").asText();

        jdbcTemplate.update(
            "DELETE FROM iss_atividade_servico WHERE atividade_id = ? AND servico_id = ?::uuid",
            java.util.UUID.fromString("e0000001-0000-4000-8000-000000000001"),
            java.util.UUID.fromString(SERVICO_ID)
        );

        mockMvc.perform(post("/api/iss/atividades-servicos")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "atividadeId": "e0000001-0000-4000-8000-000000000001",
                      "servicoId": "%s",
                      "localIncidenciaId": "%s",
                      "aliquota": 3.5,
                      "tributavel": true,
                      "imune": false,
                      "deducao": false,
                      "substitutoTributario": false,
                      "retencaoFonte": false
                    }
                    """.formatted(SERVICO_ID, localIncidenciaId)))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/iss/notas-fiscais/emitir")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "contribuinteId": "%s",
                      "tomadorId": "%s",
                      "servicoId": "%s",
                      "atividadeId": "e0000001-0000-4000-8000-000000000001",
                      "competencia": "2024-06-01",
                      "valorServico": 10000,
                      "valorDeducoes": 0,
                      "receitaBrutaAcumulada12Meses": 200000
                    }
                    """.formatted(contribuinteId, tomadorId, SERVICO_ID)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.aliquotaAplicada").value(3.5))
            .andExpect(jsonPath("$.valorIss").value(350.00));
    }

    @Test
    void deveEmitirNotaComRetencaoFonteDoCatalogoAtividadeServico() throws Exception {
        String token = login();
        String pessoaContribuinteId = cadastrarPessoaJuridica(token, "19.876.543/0001-03", "Empresa Retencao NFS-e");
        String contribuinteId = cadastrarEAprovarCredenciamento(token, pessoaContribuinteId, "876544");
        String pessoaTomadorId = cadastrarPessoaFisica(token, "100.000.010-90", "Tomador Retencao");

        String corpoTomador = mockMvc.perform(post("/api/iss/tomadores")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"pessoaId\": \"%s\"}".formatted(pessoaTomadorId)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        String tomadorId = objectMapper.readTree(corpoTomador).get("id").asText();

        String localIncidenciaId = objectMapper.readTree(
            mockMvc.perform(get("/api/iss/locais-incidencia")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()
        ).get(0).get("id").asText();

        jdbcTemplate.update(
            "DELETE FROM iss_atividade_servico WHERE atividade_id = ? AND servico_id = ?::uuid",
            java.util.UUID.fromString("e0000001-0000-4000-8000-000000000001"),
            java.util.UUID.fromString(SERVICO_ID)
        );

        mockMvc.perform(post("/api/iss/atividades-servicos")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "atividadeId": "e0000001-0000-4000-8000-000000000001",
                      "servicoId": "%s",
                      "localIncidenciaId": "%s",
                      "aliquota": 3.5,
                      "tributavel": true,
                      "imune": false,
                      "deducao": false,
                      "substitutoTributario": false,
                      "retencaoFonte": true
                    }
                    """.formatted(SERVICO_ID, localIncidenciaId)))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/iss/notas-fiscais/emitir")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "contribuinteId": "%s",
                      "tomadorId": "%s",
                      "servicoId": "%s",
                      "atividadeId": "e0000001-0000-4000-8000-000000000001",
                      "competencia": "2024-06-01",
                      "valorServico": 10000,
                      "valorDeducoes": 0,
                      "receitaBrutaAcumulada12Meses": 200000,
                      "valorIr": 10
                    }
                    """.formatted(contribuinteId, tomadorId, SERVICO_ID)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.issRetidoFonte").value(true))
            .andExpect(jsonPath("$.valorIr").value(10.00));
    }

    @Test
    void deveEmitirListarECancelarNotaFiscal() throws Exception {
        String token = login();
        String pessoaContribuinteId = cadastrarPessoaJuridica(token, "33.444.555/0001-81", "Empresa NFS-e Teste");
        String contribuinteId = cadastrarEAprovarCredenciamento(token, pessoaContribuinteId);
        String pessoaTomadorId = cadastrarPessoaFisica(token, "100.000.001-08", "João Tomador NFS-e");

        String corpoTomador = mockMvc.perform(post("/api/iss/tomadores")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"pessoaId\": \"%s\"}".formatted(pessoaTomadorId)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        String tomadorId = objectMapper.readTree(corpoTomador).get("id").asText();

        String corpoNota = mockMvc.perform(post("/api/iss/notas-fiscais/emitir")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "contribuinteId": "%s",
                      "tomadorId": "%s",
                      "servicoId": "%s",
                      "competencia": "2024-06-01",
                      "valorServico": 10000,
                      "valorDeducoes": 0,
                      "receitaBrutaAcumulada12Meses": 200000
                    }
                    """.formatted(contribuinteId, tomadorId, SERVICO_ID)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("EMITIDA"))
            .andExpect(jsonPath("$.numero").isNumber())
            .andReturn().getResponse().getContentAsString();

        String notaId = objectMapper.readTree(corpoNota).get("id").asText();

        mockMvc.perform(get("/api/iss/notas-fiscais")
                .header("Authorization", "Bearer " + token)
                .param("contribuinteId", contribuinteId)
                .param("competencia", "2024-06")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].situacao").value("EMITIDA"));

        mockMvc.perform(post("/api/iss/notas-fiscais/%s/cancelar".formatted(notaId))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"motivo\": \"Erro nos dados do tomador.\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CANCELADA"));
    }

    private String cadastrarEAprovarCredenciamento(String token, String pessoaId) throws Exception {
        return cadastrarEAprovarCredenciamento(token, pessoaId, "654321");
    }

    private String cadastrarEAprovarCredenciamento(String token, String pessoaId, String inscricaoMunicipal) throws Exception {
        String corpoContribuinte = mockMvc.perform(post("/api/iss/contribuintes")
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
}
