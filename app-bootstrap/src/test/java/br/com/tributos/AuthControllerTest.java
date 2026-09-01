package br.com.tributos;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.time.Instant;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base32;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import br.com.tributos.support.AbstractIntegrationTest;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import com.icegreen.greenmail.util.GreenMailUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Ponta a ponta do módulo de autenticação: login, refresh e o ciclo completo de
 * habilitação de MFA (gera o segredo, calcula um código TOTP válido igual um app real
 * faria, confirma, e comprova que o próximo login passa a exigir o desafio). Usuário
 * semeado por V4__seed_rbac_demo.sql (tenant "demo", login "admin", senha "Demo@123").
 */
class AuthControllerTest extends AbstractIntegrationTest {

    private static final String TENANT_SLUG = "demo";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void restaurarAdminDemoSemMfa() {
        jdbcTemplate.update("""
            UPDATE usuario
               SET mfa_habilitado = false,
                   mfa_tipo = NULL,
                   mfa_secret = NULL,
                   mfa_codigo_expira_em = NULL
             WHERE login = 'admin'
               AND tenant_id = (SELECT id FROM tenant WHERE slug = 'demo')
            """);
    }

    @Test
    void deveNegarLoginComSenhaIncorreta() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .header("X-Tenant-Slug", TENANT_SLUG)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"login":"admin","senha":"senha-errada"}
                    """))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void deveAutenticarERenovarSessao() throws Exception {
        JsonNode loginResponse = login("admin", "Demo@123");
        assertThat(loginResponse.get("mfaNecessario").asBoolean()).isFalse();
        String refreshToken = loginResponse.get("tokens").get("refreshToken").asText();

        String respostaRefresh = mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RefreshRequestTeste(refreshToken))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isNotEmpty())
            .andReturn().getResponse().getContentAsString();

        JsonNode novosTokens = objectMapper.readTree(respostaRefresh);
        assertThat(novosTokens.get("refreshToken").asText()).isNotEqualTo(refreshToken);

        // Token antigo já foi rotacionado — reapresentá-lo deve falhar.
        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RefreshRequestTeste(refreshToken))))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void deveHabilitarMfaPorEmailEExigirCodigoNoProximoLogin() throws Exception {
        JsonNode primeiroLogin = login("admin", "Demo@123");
        String accessToken = primeiroLogin.get("tokens").get("accessToken").asText();

        mockMvc.perform(post("/api/auth/mfa/habilitar")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipo\":\"EMAIL\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tipo").value("EMAIL"));

        String codigoHabilitacao = extrairCodigoDoUltimoEmail();
        mockMvc.perform(post("/api/auth/mfa/confirmar")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"codigo\":\"" + codigoHabilitacao + "\"}"))
            .andExpect(status().isNoContent());

        JsonNode loginComMfa = login("admin", "Demo@123");
        assertThat(loginComMfa.get("mfaNecessario").asBoolean()).isTrue();
        String tokenMfaPendente = loginComMfa.get("tokenMfaPendente").asText();
        String codigoLogin = extrairCodigoDoUltimoEmail();

        mockMvc.perform(post("/api/auth/mfa/verificar")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tokenMfaPendente\":\"" + tokenMfaPendente + "\",\"codigo\":\"" + codigoLogin + "\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    private static String extrairCodigoDoUltimoEmail() throws Exception {
        var mensagens = br.com.tributos.support.AbstractIntegrationTest.GREEN_MAIL.getReceivedMessages();
        assertThat(mensagens).isNotEmpty();
        String corpo = GreenMailUtil.getBody(mensagens[mensagens.length - 1]);
        Matcher matcher = Pattern.compile("\\b(\\d{6})\\b").matcher(corpo);
        assertThat(matcher.find()).isTrue();
        return matcher.group(1);
    }

    @Test
    void deveHabilitarMfaEExigirCodigoNoProximoLogin() throws Exception {
        JsonNode primeiroLogin = login("admin", "Demo@123");
        String accessToken = primeiroLogin.get("tokens").get("accessToken").asText();

        String respostaHabilitar = mockMvc.perform(post("/api/auth/mfa/habilitar")
                .header("Authorization", "Bearer " + accessToken))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        String segredo = objectMapper.readTree(respostaHabilitar).get("segredo").asText();

        mockMvc.perform(post("/api/auth/mfa/confirmar")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"codigo\":\"" + codigoTotpAtual(segredo) + "\"}"))
            .andExpect(status().isNoContent());

        JsonNode loginComMfa = login("admin", "Demo@123");
        assertThat(loginComMfa.get("mfaNecessario").asBoolean()).isTrue();
        String tokenMfaPendente = loginComMfa.get("tokenMfaPendente").asText();

        mockMvc.perform(post("/api/auth/mfa/verificar")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tokenMfaPendente\":\"" + tokenMfaPendente + "\",\"codigo\":\"" + codigoTotpAtual(segredo) + "\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    private JsonNode login(String login, String senha) throws Exception {
        String corpo = mockMvc.perform(post("/api/auth/login")
                .header("X-Tenant-Slug", TENANT_SLUG)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"" + login + "\",\"senha\":\"" + senha + "\"}"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo);
    }

    /** Mesmo algoritmo (RFC 6238) de {@code TotpVerificadorMfa}, reimplementado aqui de propósito: este teste representa o app autenticador do usuário, um sistema completamente
     * independente do backend — não deveria depender de uma classe interna do módulo platform-identity para calcular o código. */
    private static String codigoTotpAtual(String segredoBase32) throws GeneralSecurityException {
        byte[] chave = new Base32().decode(segredoBase32);
        long contador = Instant.now().getEpochSecond() / 30;
        byte[] dadosContador = ByteBuffer.allocate(8).putLong(contador).array();

        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(chave, "HmacSHA1"));
        byte[] hash = mac.doFinal(dadosContador);

        int deslocamento = hash[hash.length - 1] & 0x0F;
        int binario = ((hash[deslocamento] & 0x7F) << 24)
            | ((hash[deslocamento + 1] & 0xFF) << 16)
            | ((hash[deslocamento + 2] & 0xFF) << 8)
            | (hash[deslocamento + 3] & 0xFF);

        int codigo = binario % 1_000_000;
        return String.format("%06d", codigo);
    }

    private record RefreshRequestTeste(String refreshToken) {
    }
}
