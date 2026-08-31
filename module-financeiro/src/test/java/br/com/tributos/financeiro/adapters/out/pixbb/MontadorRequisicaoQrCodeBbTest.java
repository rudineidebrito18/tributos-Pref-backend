package br.com.tributos.financeiro.adapters.out.pixbb;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.tributos.financeiro.application.ports.GatewayPix.ComandoGerarQrCode;
import br.com.tributos.kernel.exception.RegraNegocioException;
import br.com.tributos.kernel.pixbb.CredenciaisPixBb;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MontadorRequisicaoQrCodeBbTest {

    private static final ObjectMapper JSON = new ObjectMapper();
    private final MontadorRequisicaoQrCodeBb montador = new MontadorRequisicaoQrCodeBb();

    @Test
    void deveFormatarValorComDuasCasas() throws Exception {
        String json = montador.montarJson(comando("N", "ABC123", "07512345678", null, new BigDecimal("1000")));
        JsonNode node = JSON.readTree(json);
        assertThat(node.path("valorOriginalSolicitacao").asString()).isEqualTo("1000.00");
    }

    @Test
    void deveTruncarDescricaoEm140Caracteres() throws Exception {
        String descricao = "X".repeat(200);
        String json = montador.montarJson(comandoComDescricao("N", "ABC123", descricao));
        JsonNode node = JSON.readTree(json);
        assertThat(node.path("descricaoSolicitacaoPagamento").asString()).hasSize(140);
    }

    @Test
    void deveRemoverZerosEsquerdaDoCpf() throws Exception {
        String json = montador.montarJson(comando("N", "ABC123", "07512345678", null, new BigDecimal("10")));
        JsonNode node = JSON.readTree(json);
        assertThat(node.path("cpfDevedor").asString()).isEqualTo("7512345678");
    }

    @Test
    void deveFormatarDataVencimento() throws Exception {
        String json = montador.montarJson(comando("N", "ABC123", null, null, new BigDecimal("10")));
        JsonNode node = JSON.readTree(json);
        assertThat(node.path("dataVencimentoSolicitacao").asString()).isEqualTo("31.12.2026");
    }

    @Test
    void deveRejeitarCodigo44ComIndicadorN() {
        assertThatThrownBy(() -> montador.validarAntesDeChamar(
            comando("N", "1".repeat(44), null, null, new BigDecimal("10"))
        ))
            .isInstanceOf(RegraNegocioException.class);
    }

    private static ComandoGerarQrCode comando(
        String indicador,
        String codigoGuia,
        String cpf,
        String cnpj,
        BigDecimal valor
    ) {
        return new ComandoGerarQrCode(
            credenciais(),
            "dev-key",
            "123456",
            "chave-pix",
            indicador,
            UUID.randomUUID(),
            valor,
            LocalDate.of(2026, 12, 31),
            codigoGuia,
            "Descrição teste",
            "Nome Devedor",
            cpf,
            cnpj
        );
    }

    private static ComandoGerarQrCode comandoComDescricao(String indicador, String codigoGuia, String descricao) {
        return new ComandoGerarQrCode(
            credenciais(),
            "dev-key",
            "123456",
            "chave-pix",
            indicador,
            UUID.randomUUID(),
            new BigDecimal("10.00"),
            LocalDate.of(2026, 12, 31),
            codigoGuia,
            descricao,
            "Nome",
            null,
            null
        );
    }

    private static CredenciaisPixBb credenciais() {
        return new CredenciaisPixBb(
            UUID.randomUUID(),
            "SANDBOX",
            "client",
            "secret",
            "scope",
            null,
            null
        );
    }
}
