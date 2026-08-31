package br.com.tributos.financeiro.adapters.out.pixbb;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import br.com.tributos.kernel.pixbb.CredenciaisPixBb;
import br.com.tributos.kernel.pixbb.ResultadoTokenPixBb;
import tools.jackson.databind.ObjectMapper;

@Component
public class BbOAuthClient {

    private static final int MARGEM_SEGUNDOS_CACHE = 60;
    private static final ObjectMapper OAUTH_JSON = new ObjectMapper();

    private final BbOAuthProperties properties;
    private final Clock clock;
    private final Map<String, EntradaCache> cache = new ConcurrentHashMap<>();

    public BbOAuthClient(
        BbOAuthProperties properties,
        Clock clock
    ) {
        this.properties = properties;
        this.clock = clock;
    }

    public ResultadoTokenPixBb obterToken(CredenciaisPixBb credenciais) {
        String chaveCache = chaveCache(credenciais);
        EntradaCache emCache = cache.get(chaveCache);
        if (emCache != null && emCache.valido(clock.instant())) {
            return emCache.resultado();
        }

        ResultadoTokenPixBb obtido = solicitarToken(credenciais);
        Instant expiraEm = clock.instant().plusSeconds(Math.max(0, obtido.expiresIn() - MARGEM_SEGUNDOS_CACHE));
        cache.put(chaveCache, new EntradaCache(obtido, expiraEm));
        return obtido;
    }

    void invalidarCache() {
        cache.clear();
    }

    private ResultadoTokenPixBb solicitarToken(CredenciaisPixBb credenciais) {
        String basic = Base64.getEncoder().encodeToString(
            (credenciais.clientId() + ":" + credenciais.clientSecret()).getBytes(StandardCharsets.UTF_8)
        );

        MultiValueMap<String, String> corpo = new LinkedMultiValueMap<>();
        corpo.add("grant_type", "client_credentials");
        corpo.add("scope", credenciais.escopos());

        RestClient cliente = RestClient.builder()
            .baseUrl(baseUrlOAuth(credenciais.ambiente()))
            .build();

        try {
            String corpoResposta = cliente.post()
                .uri("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "Basic " + basic)
                .body(corpo)
                .exchange((request, response) -> {
                    var corpoRespostaStream = response.getBody();
                    String body = corpoRespostaStream == null
                        ? ""
                        : new String(corpoRespostaStream.readAllBytes(), StandardCharsets.UTF_8);
                    if (response.getStatusCode().is2xxSuccessful()) {
                        return body;
                    }
                    throw new BbOAuthFalhaException(parseErroCorpo(body));
                });

            BbOAuthTokenResponse resposta = parseTokenResponse(corpoResposta);
            if (resposta.accessToken() == null || resposta.accessToken().isBlank()) {
                throw new BbOAuthFalhaException("Resposta OAuth do BB sem access_token.");
            }
            return new ResultadoTokenPixBb(
                resposta.accessToken(),
                resposta.expiresIn(),
                resposta.scope()
            );
        } catch (BbOAuthFalhaException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new BbOAuthFalhaException("Falha ao obter token OAuth do BB.", ex);
        }
    }

    private BbOAuthTokenResponse parseTokenResponse(String corpo) {
        if (corpo == null || corpo.isBlank()) {
            throw new BbOAuthFalhaException("Resposta OAuth do BB vazia.");
        }
        try {
            var json = OAUTH_JSON.readTree(corpo);
            return new BbOAuthTokenResponse(
                json.path("access_token").asString(null),
                json.path("expires_in").asInt(0),
                json.path("scope").asString(null)
            );
        } catch (RuntimeException ex) {
            throw new BbOAuthFalhaException("Resposta OAuth do BB inválida.", ex);
        }
    }

    private String parseErroCorpo(String corpo) {
        if (corpo == null || corpo.isBlank()) {
            return "Falha na autenticação OAuth do BB.";
        }
        try {
            var json = OAUTH_JSON.readTree(corpo);
            var descricao = json.path("error_description").asString(null);
            if (descricao != null && !descricao.isBlank()) {
                return descricao;
            }
            var erro = json.path("error").asString(null);
            if (erro != null && !erro.isBlank()) {
                return erro;
            }
        } catch (RuntimeException ignored) {
            // mantém mensagem genérica
        }
        return "Falha na autenticação OAuth do BB.";
    }

    private String baseUrlOAuth(String ambiente) {
        if ("PRODUCAO".equalsIgnoreCase(ambiente)) {
            return properties.producaoBaseUrl();
        }
        return properties.homologacaoBaseUrl();
    }

    private static String chaveCache(CredenciaisPixBb credenciais) {
        return credenciais.tenantId() + "|" + credenciais.ambiente();
    }

    private record EntradaCache(ResultadoTokenPixBb resultado, Instant expiraEm) {
        boolean valido(Instant agora) {
            return agora.isBefore(expiraEm);
        }
    }
}
