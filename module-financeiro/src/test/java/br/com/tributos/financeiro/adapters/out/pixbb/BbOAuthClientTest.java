package br.com.tributos.financeiro.adapters.out.pixbb;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import br.com.tributos.kernel.pixbb.CredenciaisPixBb;
import br.com.tributos.kernel.pixbb.ResultadoTokenPixBb;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BbOAuthClientTest {

    private static final UUID TENANT_ID = UUID.fromString("00000000-0000-4000-8000-000000000001");
    private static final String CLIENT_ID = "client-test";
    private static final String CLIENT_SECRET = "segredo-oauth-teste";
    private static final String ACCESS_TOKEN = "access-token-xyz";

    private WireMockServer wireMock;
    private RelogioMutavel clock;
    private BbOAuthClient client;

    @BeforeEach
    void setUp() {
        wireMock = new WireMockServer(0);
        wireMock.start();
        WireMock.configureFor("localhost", wireMock.port());

        clock = new RelogioMutavel(Instant.parse("2026-08-31T12:00:00Z"));
        BbOAuthProperties properties = new BbOAuthProperties(
            "http://localhost:" + wireMock.port(),
            "http://localhost:" + wireMock.port()
        );
        client = new BbOAuthClient(properties, clock);
    }

    @AfterEach
    void tearDown() {
        wireMock.stop();
    }

    @Test
    void deveEnviarBasicAuthComClientIdESecret() {
        stubTokenOk();

        client.obterToken(credenciais("pix.arrecadacao-info"));

        String authorization = wireMock.getAllServeEvents().getFirst().getRequest().getHeader("Authorization");
        String decodificado = new String(
            Base64.getDecoder().decode(authorization.substring("Basic ".length())),
            StandardCharsets.UTF_8
        );
        assertThat(decodificado).isEqualTo(CLIENT_ID + ":" + CLIENT_SECRET);
    }

    @Test
    void deveCachearTokenAteExpirar() {
        stubTokenOk();

        CredenciaisPixBb credenciais = credenciais("pix.arrecadacao-info");
        for (int i = 0; i < 5; i++) {
            ResultadoTokenPixBb token = client.obterToken(credenciais);
            assertThat(token.accessToken()).isEqualTo(ACCESS_TOKEN);
        }

        wireMock.verify(1, postRequestedFor(urlEqualTo("/oauth/token")));

        clock.avancar(Duration.ofSeconds(541));
        client.obterToken(credenciais);
        wireMock.verify(2, postRequestedFor(urlEqualTo("/oauth/token")));
    }

    @Test
    void devePropagarErroAmigavelDoBb() {
        wireMock.stubFor(post(urlEqualTo("/oauth/token"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {"error":"invalid_request","error_description":"Software cliente não identificado."}
                    """)));

        assertThatThrownBy(() -> client.obterToken(credenciais("pix.arrecadacao-info")))
            .isInstanceOf(BbOAuthFalhaException.class)
            .hasMessage("Software cliente não identificado.");
    }

    @Test
    void naoDeveRegistrarSegredoNemTokenEmLog() {
        stubTokenOk();
        Logger logger = (Logger) LoggerFactory.getLogger(BbOAuthClient.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        logger.setLevel(ch.qos.logback.classic.Level.DEBUG);

        try {
            client.obterToken(credenciais("pix.arrecadacao-info"));
            String logs = appender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .reduce("", String::concat);
            assertThat(logs).doesNotContain(CLIENT_SECRET);
            assertThat(logs).doesNotContain(ACCESS_TOKEN);
        } finally {
            logger.detachAppender(appender);
        }
    }

    private void stubTokenOk() {
        wireMock.stubFor(post(urlEqualTo("/oauth/token"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                      "access_token": "%s",
                      "token_type": "Bearer",
                      "expires_in": 600,
                      "scope": "pix.arrecadacao-info"
                    }
                    """.formatted(ACCESS_TOKEN))));
    }

    private static CredenciaisPixBb credenciais(String escopos) {
        return new CredenciaisPixBb(
            TENANT_ID,
            "SANDBOX",
            CLIENT_ID,
            CLIENT_SECRET,
            escopos,
            null,
            null
        );
    }

    private static final class RelogioMutavel extends Clock {
        private Instant instante;

        private RelogioMutavel(Instant instante) {
            this.instante = instante;
        }

        void avancar(Duration duracao) {
            instante = instante.plus(duracao);
        }

        @Override
        public ZoneOffset getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(java.time.ZoneId zone) {
            return Clock.fixed(instante, zone);
        }

        @Override
        public Instant instant() {
            return instante;
        }
    }
}
