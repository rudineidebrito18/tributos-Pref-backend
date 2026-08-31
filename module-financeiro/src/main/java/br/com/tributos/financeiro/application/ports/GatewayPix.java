package br.com.tributos.financeiro.application.ports;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import br.com.tributos.kernel.pixbb.CredenciaisPixBb;

/** Porta de saída para a API PIX de arrecadação do Banco do Brasil. */
public interface GatewayPix {

    RespostaQrCode gerarQrCode(ComandoGerarQrCode comando);

    StatusCobrancaPix consultarPorTxid(ConsultaPixContexto contexto, String txid);

    List<PagamentoPix> consultarPagamentos(ConsultaPixContexto contexto, String txid);

    void baixarQrCode(ConsultaPixContexto contexto, String txid);

    record ComandoGerarQrCode(
        CredenciaisPixBb credenciais,
        String developerApplicationKey,
        String numeroConvenio,
        String chavePix,
        String indicadorCodigoBarras,
        UUID guiaId,
        BigDecimal valor,
        LocalDate dataVencimento,
        String codigoGuiaRecebimento,
        String descricao,
        String nomeDevedor,
        String cpfDevedor,
        String cnpjDevedor
    ) {
    }

    record ConsultaPixContexto(
        CredenciaisPixBb credenciais,
        String developerApplicationKey
    ) {
    }

    record RespostaQrCode(
        String txid,
        String qrCodePayload,
        String pixLink,
        String estadoSolicitacao
    ) {
    }

    record StatusCobrancaPix(
        String txid,
        String estadoSolicitacao
    ) {
    }

    record PagamentoPix(
        String endToEndId,
        String valor,
        String horario
    ) {
    }
}
