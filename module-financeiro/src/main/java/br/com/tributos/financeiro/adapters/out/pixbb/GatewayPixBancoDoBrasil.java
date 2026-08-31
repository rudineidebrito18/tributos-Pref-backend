package br.com.tributos.financeiro.adapters.out.pixbb;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
import tools.jackson.databind.JsonNode;
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
        String corpo = executarGet(contexto, "/arrecadacao-qrcodes/" + URLEncoder.encode(txid, StandardCharsets.UTF_8));
        return parseStatusCobranca(corpo, txid);
    }

    @Override
    public List<PagamentoPix> consultarPagamentos(ConsultaPixContexto contexto, String txid) {
        String corpo = executarGet(contexto, "/arrecadacao-qrcodes/pagamentos/" + URLEncoder.encode(txid, StandardCharsets.UTF_8));
        return parsePagamentos(corpo);
    }

    @Override
    public void baixarQrCode(ConsultaPixContexto contexto, String txid) {
        throw new UnsupportedOperationException("Baixa de QR Code PIX no BB será implementada em etapa futura.");
    }

    private String executarGet(ConsultaPixContexto contexto, String path) {
        String token = bbOAuthClient.obterToken(contexto.credenciais()).accessToken();
        String baseUrl = baseUrlApi(contexto.credenciais().ambiente());
        String url = baseUrl + path + "?gw-dev-app-key="
            + URLEncoder.encode(contexto.developerApplicationKey(), StandardCharsets.UTF_8);

        HttpClient cliente = httpClientFactory.criarHttpClient(contexto.credenciais(), baseUrl);
        HttpRequest requisicao = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bearer " + token)
            .GET()
            .build();

        try {
            HttpResponse<String> resposta = cliente.send(requisicao, HttpResponse.BodyHandlers.ofString());
            if (resposta.statusCode() < 200 || resposta.statusCode() >= 300) {
                throw new BbPixApiFalhaException(parseErro(resposta.body()));
            }
            return resposta.body();
        } catch (BbPixApiFalhaException ex) {
            throw ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new BbPixApiFalhaException("Falha ao consultar PIX no BB.", ex);
        } catch (IOException ex) {
            throw new BbPixApiFalhaException("Falha ao consultar PIX no BB.", ex);
        }
    }

    private StatusCobrancaPix parseStatusCobranca(String corpo, String txidFallback) {
        if (corpo == null || corpo.isBlank()) {
            throw new BbPixApiFalhaException("Resposta vazia ao consultar PIX no BB.");
        }
        try {
            JsonNode json = JSON.readTree(corpo);
            String txid = json.path("codigoConciliacaoSolicitante").asString(null);
            if (txid == null || txid.isBlank()) {
                txid = json.path("txid").asString(txidFallback);
            }
            String estado = json.path("estadoSolicitacao").asString(null);
            if (estado == null || estado.isBlank()) {
                throw new BbPixApiFalhaException("Resposta do BB sem estadoSolicitacao.");
            }
            return new StatusCobrancaPix(txid, estado);
        } catch (BbPixApiFalhaException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new BbPixApiFalhaException("Resposta inválida ao consultar PIX no BB.", ex);
        }
    }

    private List<PagamentoPix> parsePagamentos(String corpo) {
        if (corpo == null || corpo.isBlank()) {
            return List.of();
        }
        try {
            JsonNode json = JSON.readTree(corpo);
            JsonNode lista = json.isArray() ? json : localizarArrayPagamentos(json);
            if (lista == null || !lista.isArray()) {
                return List.of();
            }
            List<PagamentoPix> pagamentos = new ArrayList<>();
            for (JsonNode item : lista) {
                String endToEndId = primeiroTexto(item, "endToEndId", "endToEndID");
                String valor = primeiroTexto(item, "valor", "valorPagamento", "valorPago");
                String horario = primeiroTexto(item, "horario", "horarioPagamento", "dataPagamento");
                if (endToEndId != null && valor != null) {
                    pagamentos.add(new PagamentoPix(endToEndId, valor, horario));
                }
            }
            return pagamentos;
        } catch (RuntimeException ex) {
            throw new BbPixApiFalhaException("Resposta inválida ao consultar pagamentos PIX no BB.", ex);
        }
    }

    private static JsonNode localizarArrayPagamentos(JsonNode json) {
        for (String campo : List.of("lista", "listaPagamentos", "pagamentos", "pix")) {
            JsonNode no = json.path(campo);
            if (no.isArray()) {
                return no;
            }
        }
        return null;
    }

    private static String primeiroTexto(JsonNode no, String... campos) {
        for (String campo : campos) {
            String valor = no.path(campo).asString(null);
            if (valor != null && !valor.isBlank()) {
                return valor;
            }
        }
        return null;
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
