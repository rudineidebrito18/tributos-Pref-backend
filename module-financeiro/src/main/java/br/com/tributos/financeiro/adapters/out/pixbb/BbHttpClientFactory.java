package br.com.tributos.financeiro.adapters.out.pixbb;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;

import org.springframework.stereotype.Component;

import br.com.tributos.kernel.exception.ConfiguracaoInvalidaException;
import br.com.tributos.kernel.pixbb.CredenciaisPixBb;

/**
 * Monta e cacheia {@link SSLContext} com certificado cliente PKCS#12 para a API mTLS do BB.
 * O endpoint OAuth usa TLS comum — não passa por aqui.
 */
@Component
public class BbHttpClientFactory {

    private static final String MENSAGEM_CERTIFICADO_OBRIGATORIO =
        "certificado mTLS obrigatório para a API PIX BB";

    private final Map<String, SSLContext> sslContextCache = new ConcurrentHashMap<>();

    public SSLContext obterSslContext(CredenciaisPixBb credenciais) {
        validarCertificadoPresente(credenciais);
        String chave = credenciais.tenantId() + "|" + credenciais.ambiente();
        return sslContextCache.computeIfAbsent(chave, ignored -> montarSslContext(credenciais));
    }

    public HttpClient criarHttpClientMtls(CredenciaisPixBb credenciais) {
        return HttpClient.newBuilder()
            .sslContext(obterSslContext(credenciais))
            .build();
    }

    /** Em localhost (WireMock nos testes), usa HTTP simples; demais hosts exigem mTLS. */
    public HttpClient criarHttpClient(CredenciaisPixBb credenciais, String baseUrl) {
        if (baseUrl != null && (baseUrl.startsWith("http://localhost") || baseUrl.startsWith("http://127.0.0.1"))) {
            return HttpClient.newHttpClient();
        }
        return criarHttpClientMtls(credenciais);
    }

    void limparCache() {
        sslContextCache.clear();
    }

    private static void validarCertificadoPresente(CredenciaisPixBb credenciais) {
        if (credenciais.certificadoPath() == null || credenciais.certificadoPath().isBlank()
            || credenciais.certificadoSenha() == null || credenciais.certificadoSenha().isBlank()) {
            throw new ConfiguracaoInvalidaException(MENSAGEM_CERTIFICADO_OBRIGATORIO);
        }
    }

    private SSLContext montarSslContext(CredenciaisPixBb credenciais) {
        validarCertificadoPresente(credenciais);
        Path caminho = Path.of(credenciais.certificadoPath());
        if (!Files.isRegularFile(caminho)) {
            throw new ConfiguracaoInvalidaException(MENSAGEM_CERTIFICADO_OBRIGATORIO);
        }

        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            char[] senha = credenciais.certificadoSenha().toCharArray();
            try (InputStream entrada = Files.newInputStream(caminho)) {
                keyStore.load(entrada, senha);
            }

            javax.net.ssl.KeyManagerFactory kmf = javax.net.ssl.KeyManagerFactory.getInstance(
                javax.net.ssl.KeyManagerFactory.getDefaultAlgorithm()
            );
            kmf.init(keyStore, senha);

            KeyManager[] keyManagers = kmf.getKeyManagers();
            if (keyManagers == null || keyManagers.length == 0) {
                throw new ConfiguracaoInvalidaException(MENSAGEM_CERTIFICADO_OBRIGATORIO);
            }

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, null, null);
            return sslContext;
        } catch (IOException | java.security.GeneralSecurityException ex) {
            throw new ConfiguracaoInvalidaException(MENSAGEM_CERTIFICADO_OBRIGATORIO);
        }
    }
}
