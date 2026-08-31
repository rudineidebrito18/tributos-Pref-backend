package br.com.tributos.financeiro.adapters.out.pixbb;

import java.nio.file.Path;
import java.util.UUID;

import javax.net.ssl.SSLContext;

import org.junit.jupiter.api.Test;

import br.com.tributos.kernel.exception.ConfiguracaoInvalidaException;
import br.com.tributos.kernel.pixbb.CredenciaisPixBb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BbHttpClientFactoryTest {

    private static final UUID TENANT_ID = UUID.fromString("00000000-0000-4000-8000-000000000002");
    private static final Path CERTIFICADO = Path.of("src/test/resources/fixtures/pixbb/cliente-teste.p12");

    private final BbHttpClientFactory factory = new BbHttpClientFactory();

    @Test
    void deveMontarSslContextQuandoCertificadoValido() {
        CredenciaisPixBb credenciais = credenciaisComCertificado();

        SSLContext sslContext = factory.obterSslContext(credenciais);

        assertThat(sslContext).isNotNull();
        assertThat(sslContext.getProtocol()).isEqualTo("TLS");
    }

    @Test
    void deveFalharRapidoSemCertificado() {
        CredenciaisPixBb credenciais = new CredenciaisPixBb(
            TENANT_ID,
            "SANDBOX",
            "client",
            "secret",
            "pix.arrecadacao-info",
            null,
            null
        );

        assertThatThrownBy(() -> factory.obterSslContext(credenciais))
            .isInstanceOf(ConfiguracaoInvalidaException.class)
            .hasMessage("certificado mTLS obrigatório para a API PIX BB");
    }

    @Test
    void deveReutilizarMesmoSslContextPorTenantEAmbiente() {
        CredenciaisPixBb credenciais = credenciaisComCertificado();

        SSLContext primeiro = factory.obterSslContext(credenciais);
        SSLContext segundo = factory.obterSslContext(credenciais);

        assertThat(primeiro).isSameAs(segundo);
    }

    private static CredenciaisPixBb credenciaisComCertificado() {
        return new CredenciaisPixBb(
            TENANT_ID,
            "SANDBOX",
            "client",
            "secret",
            "pix.arrecadacao-info",
            CERTIFICADO.toAbsolutePath().toString(),
            "testpass"
        );
    }
}
