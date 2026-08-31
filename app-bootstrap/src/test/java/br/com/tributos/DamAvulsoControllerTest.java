package br.com.tributos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import br.com.tributos.support.AbstractIntegrationTest;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DamAvulsoControllerTest extends AbstractIntegrationTest {

    private static final String TENANT_SLUG = "demo";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void deveEmitirDamAvulsoComCodigoVerificacaoETipoTributacao() throws Exception {
        String token = login("admin", "Demo@123");
        String pessoaId = cadastrarPessoaFisica(token);

        mockMvc.perform(post("/api/financeiro/guias-arrecadacao/avulso")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "contribuinteId": "%s",
                      "valor": 250.00,
                      "dataVencimento": "2026-12-31",
                      "descricao": "Taxa de fiscalização",
                      "tipoTributo": "OUTROS",
                      "tipoTributacao": "TRIBUTAVEL"
                    }
                    """.formatted(pessoaId)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.tipoTributo").value("OUTROS"))
            .andExpect(jsonPath("$.tipoTributacao").value("TRIBUTAVEL"))
            .andExpect(jsonPath("$.tipoTributacaoDescricao").value("TRIBUTÁVEL"))
            .andExpect(jsonPath("$.codigoVerificacao").isString())
            .andExpect(jsonPath("$.codigoVerificacao").value(org.hamcrest.Matchers.matchesPattern("[A-Z0-9]{20}")))
            .andExpect(jsonPath("$.origemTipo").value("AVULSO"));
    }

    @Test
    void fiscalDeveIsentarECancelarDamAvulso() throws Exception {
        String token = login("admin", "Demo@123");
        String guiaId = criarDamAvulso(token);

        mockMvc.perform(post("/api/financeiro/guias-arrecadacao/" + guiaId + "/isentar")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.situacao").value("ISENTA"))
            .andExpect(jsonPath("$.valorPago").value(nullValue()));

        String guia2 = criarDamAvulso(token);
        mockMvc.perform(post("/api/financeiro/guias-arrecadacao/" + guia2 + "/cancelar")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.situacao").value("CANCELADA"));
    }

    @Test
    void atendenteNaoDeveIsentarDamAvulso() throws Exception {
        String tokenAdmin = login("admin", "Demo@123");
        String guiaId = criarDamAvulso(tokenAdmin);
        criarUsuarioAtendente();

        String tokenAtendente = login("atendente", "Demo@123");
        mockMvc.perform(post("/api/financeiro/guias-arrecadacao/" + guiaId + "/isentar")
                .header("Authorization", "Bearer " + tokenAtendente))
            .andExpect(status().isForbidden());
    }

    @Test
    void deveListarSomenteDamAvulsoPorOrigem() throws Exception {
        String token = login("admin", "Demo@123");
        criarDamAvulso(token);

        mockMvc.perform(get("/api/financeiro/guias-arrecadacao")
                .header("Authorization", "Bearer " + token)
                .param("origemTipo", "AVULSO"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(org.hamcrest.Matchers.greaterThanOrEqualTo(1))))
            .andExpect(jsonPath("$.content[0].origemTipo").value("AVULSO"));
    }

    private String criarDamAvulso(String token) throws Exception {
        String pessoaId = cadastrarPessoaFisica(token);
        String corpo = mockMvc.perform(post("/api/financeiro/guias-arrecadacao/avulso")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "contribuinteId": "%s",
                      "valor": 100.00,
                      "dataVencimento": "2026-12-31",
                      "descricao": "DAM teste"
                    }
                    """.formatted(pessoaId)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("id").asText();
    }

    private String cadastrarPessoaFisica(String token) throws Exception {
        String cpf = gerarCpfUnico();
        String cidadeId = objectMapper.readTree(mockMvc.perform(get("/api/cadastro/territorio/cidades?uf=SP")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString()).get(0).get("id").asText();

        return objectMapper.readTree(mockMvc.perform(post("/api/cadastro/pessoas")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "tipoPessoa": "PF",
                      "cpfCnpj": "%s",
                      "nome": "Contribuinte DAM Avulso",
                      "ativo": true,
                      "enderecos": [{
                        "cep": "01310100",
                        "logradouro": "Rua Teste",
                        "numero": "300",
                        "bairro": "Centro",
                        "cidadeId": "%s",
                        "principal": true
                      }]
                    }
                    """.formatted(cpf, cidadeId)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString()).get("id").asText();
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
            WHERE u.login = 'atendente' AND u.tenant_id = (SELECT id FROM tenant WHERE slug = 'demo')
              AND p.nome = 'ATENDENTE'
            ON CONFLICT DO NOTHING
            """);
    }

    private String login(String usuario, String senha) throws Exception {
        return objectMapper.readTree(mockMvc.perform(post("/api/auth/login")
                .header("X-Tenant-Slug", TENANT_SLUG)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"%s\",\"senha\":\"%s\"}".formatted(usuario, senha)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString()).get("tokens").get("accessToken").asText();
    }

    private static String gerarCpfUnico() {
        String base = String.format("%09d", Math.abs(System.nanoTime()) % 1_000_000_000L);
        int digito1 = calcularDigitoVerificadorCpf(base, 10);
        int digito2 = calcularDigitoVerificadorCpf(base + digito1, 11);
        String digitos = base + digito1 + digito2;
        return "%s.%s.%s-%s".formatted(
            digitos.substring(0, 3),
            digitos.substring(3, 6),
            digitos.substring(6, 9),
            digitos.substring(9)
        );
    }

    private static int calcularDigitoVerificadorCpf(String parcial, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < parcial.length(); i++) {
            soma += Character.getNumericValue(parcial.charAt(i)) * (pesoInicial - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
