package br.com.tributos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import br.com.tributos.support.AbstractIntegrationTest;

import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Critério de aceite Sprint 6: cadastrar imóveis, emitir habite-se e certidão negativa.
 */
class ImovelControllerTest extends AbstractIntegrationTest {

    private static final String TENANT_SLUG = "demo";
    private static final String TIPO_PREDIAL_ID = "80000001-0000-4000-8000-000000000001";
    private static final String TIPO_TERRITORIAL_ID = "80000001-0000-4000-8000-000000000002";
    private static final String TIPO_EDIFICACAO_ID = "80000002-0000-4000-8000-000000000001";
    private static final String DESTINACAO_RESIDENCIAL_ID = "80000003-0000-4000-8000-000000000001";
    private static final String ZONA_CENTRO_ID = "80000006-0000-4000-8000-000000000001";
    private static final String HABITESE_TIPO_ID = "80000005-0000-4000-8000-000000000001";
    private static final String TIPO_CONTRIBUINTE_ID = "b0000001-0000-4000-8000-000000000001";
    private static final String SITUACAO_ATIVA_ID = "c0000001-0000-4000-8000-000000000001";
    private static final String REGIME_SIMPLES_ID = "d0000001-0000-4000-8000-000000000001";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void deveCadastrarImoveisEmitirHabiteseECertidaoNegativa() throws Exception {
        String token = login("admin", "Demo@123");
        String pessoaPfId = cadastrarPessoaFisica(token, "100.000.004-42", "Maria Proprietária IPTU");
        String pessoaPjId = cadastrarPessoaJuridica(token, "77.888.999/0001-81", "Empresa Proprietária IPTU");
        String contribuintePfId = cadastrarContribuinte(token, pessoaPfId, "IM0004");
        cadastrarContribuinte(token, pessoaPjId, "IM0005");
        String situacaoCndId = cadastrarSituacaoCnd(token);

        String corpoPredial = mockMvc.perform(post("/api/iptu/imoveis")
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
                    """.formatted(pessoaPfId, TIPO_PREDIAL_ID, TIPO_EDIFICACAO_ID, DESTINACAO_RESIDENCIAL_ID, ZONA_CENTRO_ID)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.situacao").value("ATIVO"))
            .andReturn().getResponse().getContentAsString();

        int numeroPredial = objectMapper.readTree(corpoPredial).get("numeroCadastro").asInt();
        String imovelPredialId = objectMapper.readTree(corpoPredial).get("id").asText();

        mockMvc.perform(post("/api/iptu/imoveis")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "proprietarioId": "%s",
                      "tipoId": "%s",
                      "zonaFiscalId": "%s",
                      "areaTerreno": 500
                    }
                    """.formatted(pessoaPjId, TIPO_TERRITORIAL_ID, ZONA_CENTRO_ID)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.numeroCadastro").value(numeroPredial + 1));

        mockMvc.perform(post("/api/iptu/imoveis/%s/habiteses".formatted(imovelPredialId))
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
                      "desconto": 0
                    }
                    """.formatted(HABITESE_TIPO_ID, contribuintePfId)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.numero").isNumber());

        mockMvc.perform(post("/api/iptu/imoveis/%s/certidoes-negativas".formatted(imovelPredialId))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "situacaoCndId": "%s"
                    }
                    """.formatted(situacaoCndId)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.numero").isNumber())
            .andExpect(jsonPath("$.codigoVerificacao").isNotEmpty());

        mockMvc.perform(get("/api/iptu/imoveis/%s/habiteses".formatted(imovelPredialId))
                .header("Authorization", "Bearer " + token)
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)));

        mockMvc.perform(get("/api/iptu/imoveis/%s/certidoes-negativas".formatted(imovelPredialId))
                .header("Authorization", "Bearer " + token)
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    void devePersistirCamposAuditadosGerenciarProprietariosObservacoesETitularidade() throws Exception {
        String tokenAdmin = login("admin", "Demo@123");
        criarUsuarioAtendente();

        String sufixo = String.valueOf(System.currentTimeMillis() % 100000);
        String pessoaPfId = cadastrarPessoaFisica(tokenAdmin, gerarCpf(sufixo + "1"), "João Proprietário E61");
        String pessoaPjId = cadastrarPessoaJuridica(tokenAdmin, gerarCnpj(sufixo + "2"), "Empresa Coproprietária E61");
        String contribuintePfId = cadastrarContribuinte(tokenAdmin, pessoaPfId, "IM" + sufixo + "1");
        String contribuintePjId = cadastrarContribuinte(tokenAdmin, pessoaPjId, "IM" + sufixo + "2");
        String pessoaTerceiraId = cadastrarPessoaFisica(tokenAdmin, gerarCpf(sufixo + "3"), "Terceiro Coproprietário");
        String contribuinteTerceiroId = cadastrarContribuinte(tokenAdmin, pessoaTerceiraId, "IM" + sufixo + "3");

        String cidadeId = objectMapper.readTree(
            mockMvc.perform(get("/api/cadastro/territorio/cidades?uf=SP")
                    .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()
        ).get(0).get("id").asText();

        String enderecoCorrespondenciaId = objectMapper.readTree(
            mockMvc.perform(post("/api/cadastro/pessoas")
                    .header("Authorization", "Bearer " + tokenAdmin)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "tipoPessoa": "PF",
                          "cpfCnpj": "%s",
                          "nome": "Endereço Correspondência",
                          "ativo": true,
                          "enderecos": [{
                            "cep": "04547000",
                            "logradouro": "Av Faria Lima",
                            "numero": "1000",
                            "bairro": "Itaim",
                            "cidadeId": "%s",
                            "principal": true
                          }]
                        }
                        """.formatted(gerarCpf(sufixo + "9"), cidadeId)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString()
        ).get("enderecos").get(0).get("id").asText();

        String corpoImovel = mockMvc.perform(post("/api/iptu/imoveis")
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "tipoId": "%s",
                      "areaConstruida": 150,
                      "areaTerreno": 220,
                      "tipoEdificacaoId": "%s",
                      "destinacaoId": "%s",
                      "zonaFiscalId": "%s",
                      "valorVenalTerreno": 180000,
                      "valorVenalConstrucao": 320000,
                      "anoExercicio": 2024,
                      "dataInclusao": "2024-01-15",
                      "areaTotal": 370,
                      "frente": 12.5,
                      "fundos": 12.5,
                      "ladoEsquerdo": 30,
                      "ladoDireito": 30,
                      "quadra": "Q1",
                      "lote": "L2",
                      "loteamento": "Jardim E61",
                      "edificio": "Torre A",
                      "bloco": "B1",
                      "sala": "S3",
                      "apartamento": "101",
                      "valorVenalUnidade": 500000,
                      "valorAvaliacao": 520000,
                      "enderecoCorrespondenciaId": "%s",
                      "observacao": "Imóvel teste E6.1"
                    }
                    """.formatted(
                    TIPO_PREDIAL_ID,
                    TIPO_EDIFICACAO_ID,
                    DESTINACAO_RESIDENCIAL_ID,
                    ZONA_CENTRO_ID,
                    enderecoCorrespondenciaId
                )))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.anoExercicio").value(2024))
            .andExpect(jsonPath("$.areaTotal").value(370))
            .andExpect(jsonPath("$.quadra").value("Q1"))
            .andExpect(jsonPath("$.valorVenalUnidade").value(500000))
            .andExpect(jsonPath("$.valorAvaliacao").value(520000))
            .andExpect(jsonPath("$.enderecoCorrespondenciaId").value(enderecoCorrespondenciaId))
            .andExpect(jsonPath("$.observacao").value("Imóvel teste E6.1"))
            .andReturn().getResponse().getContentAsString();

        String imovelId = objectMapper.readTree(corpoImovel).get("id").asText();

        mockMvc.perform(get("/api/iptu/imoveis/" + imovelId)
                .header("Authorization", "Bearer " + tokenAdmin))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.loteamento").value("Jardim E61"))
            .andExpect(jsonPath("$.apartamento").value("101"));

        mockMvc.perform(get("/api/iptu/imoveis/%s/titularidade".formatted(imovelId))
                .header("Authorization", "Bearer " + tokenAdmin))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));

        mockMvc.perform(post("/api/iptu/imoveis/%s/proprietarios".formatted(imovelId))
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "contribuinteId": "%s",
                      "porcentagem": 50,
                      "proprietarioPrincipal": true
                    }
                    """.formatted(contribuintePfId)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.porcentagem").value(50));

        mockMvc.perform(post("/api/iptu/imoveis/%s/proprietarios".formatted(imovelId))
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "contribuinteId": "%s",
                      "porcentagem": 50,
                      "proprietarioPrincipal": false
                    }
                    """.formatted(contribuintePjId)))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/api/iptu/imoveis/%s/proprietarios".formatted(imovelId))
                .header("Authorization", "Bearer " + tokenAdmin))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));

        mockMvc.perform(post("/api/iptu/imoveis/%s/proprietarios".formatted(imovelId))
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "contribuinteId": "%s",
                      "porcentagem": 20,
                      "proprietarioPrincipal": false
                    }
                    """.formatted(contribuinteTerceiroId)))
            .andExpect(status().isUnprocessableEntity());

        String tokenAtendente = login("atendente", "Demo@123");
        mockMvc.perform(post("/api/iptu/imoveis/%s/proprietarios".formatted(imovelId))
                .header("Authorization", "Bearer " + tokenAtendente)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "contribuinteId": "%s",
                      "porcentagem": 10,
                      "proprietarioPrincipal": false
                    }
                    """.formatted(contribuintePjId)))
            .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/iptu/imoveis/%s/observacoes".formatted(imovelId))
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"texto\": \"Observação registrada pelo admin.\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.usuario").value("admin"))
            .andExpect(jsonPath("$.observacao").value("Observação registrada pelo admin."));

        mockMvc.perform(get("/api/iptu/imoveis/%s/observacoes".formatted(imovelId))
                .header("Authorization", "Bearer " + tokenAdmin))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].usuario").value("admin"));

        Integer registrosAuditoria = jdbcTemplate.queryForObject(
            """
            SELECT COUNT(*) FROM log_auditoria
            WHERE entidade = 'imovel_proprietario'
              AND acao = 'ADICIONAR'
            """,
            Integer.class
        );
        assertThat(registrosAuditoria).isGreaterThan(0);
    }

    private String cadastrarSituacaoCnd(String token) throws Exception {
        String sufixo = String.valueOf(System.currentTimeMillis() % 100000);
        String corpo = mockMvc.perform(post("/api/iss/situacoes-cnd")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "descricao": "Nada consta imóvel demo %s",
                      "titulo": "Negativa demo %s",
                      "ativo": true
                    }
                    """.formatted(sufixo, sufixo)))
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

    private void criarUsuarioAtendente() {
        jdbcTemplate.update("""
            INSERT INTO usuario (id, tenant_id, login, email, senha_hash, mfa_habilitado, ativo)
            SELECT gen_random_uuid(), t.id, 'atendente', 'atendente@demo.gov.br',
                   '$2a$10$sSqLb.JalB61zOLrMb/9wuqqEdjKQgkhuCqYZmhuSDizy0hCd3S7K', false, true
            FROM tenant t WHERE t.slug = 'demo'
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.update("""
            INSERT INTO usuario_papel (usuario_id, papel_id)
            SELECT u.id, p.id
            FROM usuario u, papel p
            WHERE u.login = 'atendente' AND p.nome = 'ATENDENTE'
            ON CONFLICT DO NOTHING
            """);
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
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int soma = 0;
        for (int i = 0; i < 12; i++) {
            soma += Character.getNumericValue(base.charAt(i)) * pesos1[i];
        }
        int digito1 = soma % 11 < 2 ? 0 : 11 - (soma % 11);
        soma = 0;
        String comPrimeiroDigito = base + digito1;
        for (int i = 0; i < 13; i++) {
            soma += Character.getNumericValue(comPrimeiroDigito.charAt(i)) * pesos2[i];
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

    private String login(String usuario, String senha) throws Exception {
        String corpo = mockMvc.perform(post("/api/auth/login")
                .header("X-Tenant-Slug", TENANT_SLUG)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"%s\",\"senha\":\"%s\"}".formatted(usuario, senha)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("tokens").get("accessToken").asText();
    }
}
