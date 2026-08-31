package br.com.tributos.identity.adapters.in.web.dto;

import java.time.Instant;
import java.util.UUID;

import br.com.tributos.identity.application.ListarCaixaService.MensagemCaixaItem;

public record MensagemInternaResumoResponse(
    UUID id,
    String assunto,
    String usuario,
    Instant criadoEm,
    Instant lidaEm,
    Instant arquivadaEm
) {

    public static MensagemInternaResumoResponse de(MensagemCaixaItem item) {
        return new MensagemInternaResumoResponse(
            item.id(),
            item.assunto(),
            item.usuario(),
            item.criadoEm(),
            item.lidaEm(),
            item.arquivadaEm()
        );
    }
}
