package br.com.tributos.financeiro.application.webhook;

import java.util.List;

public record WebhookPixComando(List<PixRecebidoComando> pix) {
}
