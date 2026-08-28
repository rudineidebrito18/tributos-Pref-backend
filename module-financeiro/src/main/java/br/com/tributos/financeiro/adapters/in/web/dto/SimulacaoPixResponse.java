package br.com.tributos.financeiro.adapters.in.web.dto;

public record SimulacaoPixResponse(
    String pixTxid,
    String codigoBarras,
    String qrCodePayload
) {
}
