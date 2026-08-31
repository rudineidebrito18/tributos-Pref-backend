package br.com.tributos.financeiro.adapters.out.pixbb;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import br.com.tributos.financeiro.application.ports.GatewayPix;
import br.com.tributos.financeiro.application.ports.GatewayPix.ComandoGerarQrCode;
import br.com.tributos.financeiro.application.ports.GatewayPix.ConsultaPixContexto;
import br.com.tributos.financeiro.application.ports.GatewayPix.PagamentoPix;
import br.com.tributos.financeiro.application.ports.GatewayPix.RespostaQrCode;
import br.com.tributos.financeiro.application.ports.GatewayPix.StatusCobrancaPix;

/** Implementação local para desenvolvimento e testes sem credenciais reais do BB. */
@Component
@Profile("dev-sem-bb")
public class GatewayPixFalso implements GatewayPix {

    @Override
    public RespostaQrCode gerarQrCode(ComandoGerarQrCode comando) {
        String txid = "FAKE-" + comando.guiaId().toString().replace("-", "").substring(0, 27);
        String payload = "00020126580014br.gov.bcb.pix0136" + txid
            + "5204000053039865802BR5925TRIBUTOS FAKE6009SAO PAULO62070503***6304ABCD";
        return new RespostaQrCode(
            txid,
            payload,
            "https://fake.pix.local/" + txid,
            "ATIVA"
        );
    }

    @Override
    public StatusCobrancaPix consultarPorTxid(ConsultaPixContexto contexto, String txid) {
        if (txid != null && txid.contains("CONCLUIDA")) {
            return new StatusCobrancaPix(txid, "CONCLUIDA");
        }
        return new StatusCobrancaPix(txid, "ATIVA");
    }

    @Override
    public List<PagamentoPix> consultarPagamentos(ConsultaPixContexto contexto, String txid) {
        if (txid != null && txid.contains("CONCLUIDA")) {
            return List.of(new PagamentoPix(
                "E2E-FAKE-CONCILIACAO",
                "100.00",
                "2026-08-31T12:00:00-03:00"
            ));
        }
        return List.of();
    }

    @Override
    public void baixarQrCode(ConsultaPixContexto contexto, String txid) {
        // noop
    }
}
