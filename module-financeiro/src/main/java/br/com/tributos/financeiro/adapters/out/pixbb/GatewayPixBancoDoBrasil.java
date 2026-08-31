package br.com.tributos.financeiro.adapters.out.pixbb;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import br.com.tributos.financeiro.application.ports.GatewayPix;
import br.com.tributos.financeiro.application.ports.GatewayPix.ComandoGerarQrCode;
import br.com.tributos.financeiro.application.ports.GatewayPix.ConsultaPixContexto;
import br.com.tributos.financeiro.application.ports.GatewayPix.PagamentoPix;
import br.com.tributos.financeiro.application.ports.GatewayPix.RespostaQrCode;
import br.com.tributos.financeiro.application.ports.GatewayPix.StatusCobrancaPix;
import br.com.tributos.kernel.pixbb.CredenciaisPixBb;
import tools.jackson.databind.ObjectMapper;

@Component
@Profile("!dev-sem-bb")
public class GatewayPixBancoDoBrasil implements GatewayPix {

    private static final ObjectMapper JSON = new ObjectMapper();

    private final BbOAuthClient bbOAuthClient;
    private final BbHttpClientFactory httpClientFactory;
    private final BbPixApiProperties apiProperties;
    private final MontadorRequisicaoQrCodeBb montador;

    public GatewayPixBancoDoBrasil(
        BbOAuthClient bbOAuthClient,
        BbHttpClientFactory httpClientFactory,
        BbPixApiProperties apiProperties,
        MontadorRequisicaoQrCodeBb montador
    ) {
        this.bbOAuthClient = bbOAuthClient;
        this.httpClientFactory = httpClientFactory;
        this.apiProperties = apiProperties;
        this.montador = montador;
    }

    @Override
    public RespostaQrCode gerarQrCode(ComandoGerarQrCode comando) {
        montador.validarAntesDeChamar(comando);
        String token = bbOAuthClient.obterToken(comando.credenciais()).accessToken();
        String corpoRequisicao = montador.montarJson(comando);
        String baseUrl = baseUrlApi(comando.credenciais().ambiente());
        String url = baseUrl + "/arrecadacao-qrcodes?gw-dev-app-key="
            + URLEncoder.encode(comando.developerApplicationKey(), StandardCharsets.UTF_8);

        HttpClient cliente = httpClientFactory.criarHttpClient(comando.credenciais(), baseUrl);
        HttpRequest requisicao = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bearer " + token)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(corpoRequisicao))
            .build();

        try {
            HttpResponse<String> resposta = cliente.send(requisicao, HttpResponse.BodyHandlers.ofString());
            if (resposta.statusCode() < 200 || resposta.statusCode() >= 300) {
                throw new BbPixApiFalhaException(parseErro(resposta.body()));
            }
            return parseRespostaQrCode(resposta.body());
        } catch (BbPixApiFalhaException ex) {
            throw ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new BbPixApiFalhaException("Falha ao gerar QR Code PIX no BB.", ex);
        } catch (IOException ex) {
            throw new BbPixApiFalhaException("Falha ao gerar QR Code PIX no BB.", ex);
        }
    }

    @Override
    public StatusCobrancaPix consultarPorTxid(ConsultaPixContexto contexto, String txid) {
        throw new UnsupportedOperationException("Consulta PIX por txid será implementada em E5.6.");
    }

    @Override
    public List<PagamentoPix> consultarPagamentos(ConsultaPixContexto contexto, String txid) {
        throw new UnsupportedOperationException("Consulta de pagamentos PIX será implementada em E5.6.");
    }

    @Override
    public void baixarQrCode(ConsultaPixContexto contexto, String txid) {
        throw new UnsupportedOperationException("Baixa de QR Code PIX será implementada em E5.6.");
    }

    private RespostaQrCode parseRespostaQrCode(String corpo) {
        if (corpo == null || corpo.isBlank()) {
            throw new BbPixApiFalhaException("Resposta vazia ao gerar QR Code PIX no BB.");
        }
        try {
            var json = JSON.readTree(corpo);
            String txid = json.path("codigoConciliacaoSolicitante").asString(null);
            if (txid == null || txid.isBlank()) {
                txid = json.path("txid").asString(null);
            }
            String qrCode = json.path("qrCode").asString(null);
            String link = json.path("linkQrCode").asString(null);
            String estado = json.path("estadoSolicitacao").asString(null);
            if (txid == null || qrCode == null) {
                throw new BbPixApiFalhaException("Resposta do BB sem txid ou qrCode.");
            }
            return new RespostaQrCode(txid, qrCode, link, estado);
        } catch (BbPixApiFalhaException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new BbPixApiFalhaException("Resposta inválida ao gerar QR Code PIX no BB.", ex);
        }
    }

    private String parseErro(String corpo) {
        if (corpo == null || corpo.isBlank()) {
            return "Falha ao gerar QR Code PIX no BB.";
        }
        try {
            var json = JSON.readTree(corpo);
            var descricao = json.path("error_description").asString(null);
            if (descricao != null && !descricao.isBlank()) {
                return descricao;
            }
            var mensagem = json.path("mensagem").asString(null);
            if (mensagem != null && !mensagem.isBlank()) {
                return mensagem;
            }
        } catch (RuntimeException ignored) {
            // mantém genérica
        }
        return "Falha ao gerar QR Code PIX no BB.";
    }

    private String baseUrlApi(String ambiente) {
        if ("PRODUCAO".equalsIgnoreCase(ambiente)) {
            return apiProperties.producaoBaseUrl();
        }
        return apiProperties.homologacaoBaseUrl();
    }
}
