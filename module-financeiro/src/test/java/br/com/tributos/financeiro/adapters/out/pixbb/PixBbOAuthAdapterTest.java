package br.com.tributos.financeiro.adapters.out.pixbb;

import org.junit.jupiter.api.Test;

import br.com.tributos.kernel.pixbb.CredenciaisPixBb;
import br.com.tributos.kernel.pixbb.ResultadoTesteConexaoPixBb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PixBbOAuthAdapterTest {

    @Test
    void deveRetornarErroAmigavelQuandoOAuthFalhar() {
        BbOAuthClient client = mock(BbOAuthClient.class);
        CredenciaisPixBb credenciais = new CredenciaisPixBb(
            java.util.UUID.randomUUID(),
            "SANDBOX",
            "id",
            "secret",
            "pix.arrecadacao-info",
            null,
            null
        );
        when(client.obterToken(credenciais))
            .thenThrow(new BbOAuthFalhaException("Software cliente não identificado."));

        PixBbOAuthAdapter adapter = new PixBbOAuthAdapter(client);
        ResultadoTesteConexaoPixBb resultado = adapter.testarConexao(credenciais);

        assertThat(resultado.ok()).isFalse();
        assertThat(resultado.erro()).isEqualTo("Software cliente não identificado.");
    }
}
