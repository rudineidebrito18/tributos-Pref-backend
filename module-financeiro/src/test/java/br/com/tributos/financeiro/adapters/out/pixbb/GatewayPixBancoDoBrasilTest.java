package br.com.tributos.financeiro.adapters.out.pixbb;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import br.com.tributos.financeiro.application.ports.GatewayPix.ComandoGerarQrCode;
import br.com.tributos.financeiro.application.ports.GatewayPix.RespostaQrCode;
import br.com.tributos.kernel.pixbb.CredenciaisPixBb;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

class GatewayPixBancoDoBrasilTest {

    private WireMockServer wireMock;
    private GatewayPixBancoDoBrasil gateway;

    @BeforeEach
    void setUp() {
        wireMock = new WireMockServer(0);
        wireMock.start();
        WireMock.configureFor("localhost", wireMock.port());

        stubOAuth();
        stubGerarQrCode();

        BbOAuthProperties oauthProperties = new BbOAuthProperties(
            "http://localhost:" + wireMock.port(),
            "http://localhost:" + wireMock.port()
        );
        BbPixApiProperties apiProperties = new BbPixApiProperties(
            "http://localhost:" + wireMock.port() + "/v1",
            "http://localhost:" + wireMock.port() + "/v1"
        );
        Clock clock = Clock.fixed(Instant.parse("2026-08-31T12:00:00Z"), ZoneOffset.UTC);
        BbOAuthClient oauthClient = new BbOAuthClient(oauthProperties, clock);
        BbHttpClientFactory httpFactory = new BbHttpClientFactory();
        MontadorRequisicaoQrCodeBb montador = new MontadorRequisicaoQrCodeBb();
        gateway = new GatewayPixBancoDoBrasil(oauthClient, httpFactory, apiProperties, montador);
    }

    @AfterEach
    void tearDown() {
        wireMock.stop();
    }

    @Test
    void deveEnviarBearerEDevAppKey() {
        RespostaQrCode resposta = gateway.gerarQrCode(comando());

        assertThat(resposta.txid()).isEqualTo("TXID-TESTE-123");
        assertThat(resposta.qrCodePayload()).contains("000201");

        wireMock.verify(postRequestedFor(urlPathEqualTo("/v1/arrecadacao-qrcodes"))
            .withQueryParam("gw-dev-app-key", equalTo("dev-key-teste"))
            .withHeader("Authorization", equalTo("Bearer access-token-teste")));
    }

    private void stubOAuth() {
        wireMock.stubFor(WireMock.post(urlPathEqualTo("/oauth/token"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {"access_token":"access-token-teste","expires_in":600,"scope":"pix.arrecadacao-requisicao"}
                    """)));
    }

    private void stubGerarQrCode() {
        wireMock.stubFor(WireMock.post(urlPathEqualTo("/v1/arrecadacao-qrcodes"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                      "codigoConciliacaoSolicitante": "TXID-TESTE-123",
                      "estadoSolicitacao": "ATIVA",
                      "linkQrCode": "https://qrcodepix-h.bb.com.br/pix/v2/abc",
                      "qrCode": "00020126580014br.gov.bcb.pix0136TXID-TESTE-123"
                    }
                    """)));
    }

    private static ComandoGerarQrCode comando() {
        return new ComandoGerarQrCode(
            new CredenciaisPixBb(
                UUID.randomUUID(),
                "SANDBOX",
                "client-test",
                "secret-test",
                "pix.arrecadacao-requisicao",
                null,
                null
            ),
            "dev-key-teste",
            "123456",
            "00000000000000000000000000000000000000000000",
            "N",
            UUID.randomUUID(),
            new BigDecimal("150.00"),
            LocalDate.of(2026, 9, 15),
            "CODVERIF1234567890",
            "Guia ISS",
            "Empresa Teste",
            null,
            "44555666000181"
        );
    }
}
